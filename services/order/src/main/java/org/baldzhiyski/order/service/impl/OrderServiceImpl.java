package org.baldzhiyski.order.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.baldzhiyski.order.customer.CustomerClient;
import org.baldzhiyski.order.customer.CustomerRes;
import org.baldzhiyski.order.events.OrderEventPublisher;
import org.baldzhiyski.order.exception.BusinessException;
import org.baldzhiyski.order.mapper.OrderLineMapper;
import org.baldzhiyski.order.mapper.OrderMapper;
import org.baldzhiyski.order.model.Order;
import org.baldzhiyski.order.model.OrderLine;
import org.baldzhiyski.order.model.OrderStatus;
import org.baldzhiyski.order.model.PaymentMethod;
import org.baldzhiyski.order.model.req.Customer;
import org.baldzhiyski.order.model.req.OrderReq;
import org.baldzhiyski.order.model.req.PaymentCheckoutReq;
import org.baldzhiyski.order.model.req.PurchaseRequest;
import org.baldzhiyski.order.model.res.OrderRes;
import org.baldzhiyski.order.model.res.PaymentCheckoutRes;
import org.baldzhiyski.order.payment.PaymentClient;
import org.baldzhiyski.order.product.ProductClient;
import org.baldzhiyski.order.product.ProductRes;
import org.baldzhiyski.order.product.ReserveCommand;
import org.baldzhiyski.order.product.ReserveResponse;
import org.baldzhiyski.order.repository.OrderLineRepository;
import org.baldzhiyski.order.repository.OrderRepository;
import org.baldzhiyski.order.service.OrderService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderLineMapper orderLineMapper;
    private final OrderEventPublisher orderEventPublisher;
    private final OrderLineRepository orderLineRepository;
    private final PaymentClient paymentClient;

    public OrderServiceImpl(CustomerClient customerClient,
                            ProductClient productClient,
                            OrderRepository orderRepository,
                            OrderMapper orderMapper,
                            OrderLineMapper orderLineMapper,
                            OrderEventPublisher orderEventPublisher,
                            OrderLineRepository orderLineRepository,
                            PaymentClient paymentClient) {
        this.customerClient = customerClient;
        this.productClient = productClient;
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.orderLineMapper = orderLineMapper;
        this.orderEventPublisher = orderEventPublisher;
        this.orderLineRepository = orderLineRepository;
        this.paymentClient = paymentClient;
    }

    @Transactional
    public PaymentCheckoutRes createOrder(OrderReq orderReq) {
        // 1) validate customer (external)
        CustomerRes customer = customerClient.getCustomerById(orderReq.customerId());

        // 2) generate orderRef
        String orderRef = (orderReq.reference() != null) ? orderReq.reference() : UUID.randomUUID().toString();

        // 3) RESERVE (external; no stock decrement yet)
        ReserveResponse reserve = productClient.reserve(new ReserveCommand(orderRef, customer.id(), orderReq.products()));
        var pricedById = reserve.priced().stream().collect(Collectors.toMap(ProductRes::id, p -> p));

        // 4) compute total using priced response
        Map<Integer, Integer> qtyByProduct = orderReq.products().stream()
                .collect(Collectors.groupingBy(PurchaseRequest::productId, Collectors.summingInt(pr -> (int) pr.quantity())));

        BigDecimal total = reserve.priced().stream()
                .map(p -> p.finalUnitPrice().multiply(BigDecimal.valueOf(qtyByProduct.getOrDefault(p.id(), 0))))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 5) persist order + lines
        Order order = orderMapper.toEntity(orderReq);
        order.setReference(orderRef);
        order.setCustomerId(orderReq.customerId());
        order.setPaymentMethod(orderReq.paymentMethod());
        order.setTotalAmount(total);
        // Optional: order.setStatus(OrderStatus.PENDING_PAYMENT);

        Order saved = orderRepository.save(order);

        List<OrderLine> lines = orderReq.products().stream()
                .map(reqLine -> {
                    OrderLine line = orderLineMapper.toEntity(reqLine);
                    line.setOrder(saved);
                    if (!pricedById.containsKey(line.getProductId())) {
                        throw new BusinessException("No pricing for productId=" + line.getProductId());
                    }
                    return line;
                })
                .toList();
        orderLineRepository.saveAll(lines);

        // 6) Create Stripe Checkout Session via payment-service (amount in MINOR units)
        long amountMinor = total.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();

        var payReq = new PaymentCheckoutReq(
                orderRef,
                PaymentMethod.CREDIT_CARD,
                saved.getId(),
                new Customer(
                        customer.id(), customer.email(), customer.firstName(), customer.lastName()
                ),
                BigDecimal.valueOf(amountMinor) // payment-service expects .longValue() -> minor units
        );

        try {
            PaymentCheckoutRes checkout = paymentClient.createCheckoutSession(payReq);

            // Persist ONLY the sessionId (not the URL)
            saved.setSessionId(checkout.sessionId());
            orderRepository.save(saved);

            log.info("Created Checkout Session for orderRef={} sessionId={} url={}",
                    orderRef, checkout.sessionId(), checkout.url());

            // Do NOT confirm/cancel inventory here; finalize via payment events listener
            return checkout;

        } catch (Exception e) {
            // If we fail to create a checkout session, release the reservation and abort
            try { productClient.cancel(orderRef); } catch (Exception ignored) {}
            throw new BusinessException("Unable to create payment session for orderRef=" + orderRef);
        }
    }


    // -----------------------------------------------------------------------
    // Payment events listener (same queue your notification-service consumes)
    // -----------------------------------------------------------------------
    @RabbitListener(queues = "${app.mq.queues.payments}", concurrency = "3-12")
    @Transactional // ensures DB status updates participate in a tx per message
    public void onPaymentEvents(Message message, Map<String, Object> payload) {
        String rk = message.getMessageProperties().getReceivedRoutingKey(); // "payment.succeeded" | "payment.failed"

        String orderRef = asText(payload, "orderRef");
        if (orderRef == null || orderRef.isBlank()) return;

        try {
            if ("payment.succeeded".equals(rk)) {
                // 1) finalize inventory (idempotent on product service side)
                productClient.confirm(orderRef);

                // 2) mark order paid (idempotent)
                orderRepository.updateStatusByReference(orderRef, OrderStatus.PAID);

                log.info("Order {} marked PAID and inventory confirmed", orderRef);

            } else if ("payment.failed".equals(rk)) {
                // 1) release reservation
                productClient.cancel(orderRef);

                // 2) mark order payment failed
                orderRepository.updateStatusByReference(orderRef, OrderStatus.PAYMENT_FAILED);

                log.info("Order {} marked PAYMENT_FAILED and reservation cancelled", orderRef);
            }
        } catch (Exception ex) {
            // Let the listener’s error handler decide retries/DLQ
            log.error("Error handling {} for {}: {}", rk, orderRef, ex.getMessage(), ex);
            throw ex;
        }
    }

    @SuppressWarnings("unchecked")
    private static String asText(Map<String, Object> payload, String field) {
        Object v = payload.get(field);
        return (v == null) ? null : String.valueOf(v);
    }

    private static String text(JsonNode node, String field) {
        JsonNode v = (node == null) ? null : node.get(field);
        return (v == null || v.isNull()) ? null : v.asText();
    }
    // ------------------------------- Queries --------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<OrderRes> findAll() {
        return orderRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(orderMapper::toRes)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderRes> findAllByCustomerId(String customerId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(orderMapper::toRes)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderRes findById(Integer id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order %d not found".formatted(id)));
        return orderMapper.toRes(order);
    }
}
