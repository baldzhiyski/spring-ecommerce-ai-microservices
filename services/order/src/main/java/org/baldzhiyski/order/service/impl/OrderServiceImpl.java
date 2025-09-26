package org.baldzhiyski.order.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.baldzhiyski.order.customer.CustomerClient;
import org.baldzhiyski.order.events.OrderEventPublisher;
import org.baldzhiyski.order.exception.BusinessException;
import org.baldzhiyski.order.mapper.OrderLineMapper;
import org.baldzhiyski.order.mapper.OrderMapper;
import org.baldzhiyski.order.model.Order;
import org.baldzhiyski.order.model.OrderLine;
import org.baldzhiyski.order.model.req.OrderReq;
import org.baldzhiyski.order.model.req.PurchaseRequest;
import org.baldzhiyski.order.model.res.OrderRes;
import org.baldzhiyski.order.product.ProductClient;
import org.baldzhiyski.order.product.ProductRes;
import org.baldzhiyski.order.product.ReserveCommand;
import org.baldzhiyski.order.product.ReserveResponse;
import org.baldzhiyski.order.repository.OrderLineRepository;
import org.baldzhiyski.order.repository.OrderRepository;
import org.baldzhiyski.order.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderLineMapper orderLineMapper;
    private final OrderEventPublisher orderEventPublisher;
    private final OrderLineRepository orderLineRepository;

    public OrderServiceImpl(CustomerClient customerClient, ProductClient productClient, OrderRepository orderRepository, OrderMapper orderMapper, OrderLineMapper orderLineMapper, OrderEventPublisher orderEventPublisher, OrderLineRepository orderLineRepository) {
        this.customerClient = customerClient;
        this.productClient = productClient;
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.orderLineMapper = orderLineMapper;
        this.orderEventPublisher = orderEventPublisher;
        this.orderLineRepository = orderLineRepository;
    }

    @Transactional
    public Integer createOrder(OrderReq orderReq) {
        // 1) validate customer (external)
        var customer = customerClient.getCustomerById(orderReq.customerId())
                .orElseThrow(() -> new BusinessException("Customer %s not found".formatted(orderReq.customerId())));

        // 2) generate orderRef
        String orderRef = orderReq.reference() != null ? orderReq.reference() : UUID.randomUUID().toString();

        // 3) RESERVE (external; no stock decrement yet)
        ReserveResponse reserve = productClient.reserve(new ReserveCommand(orderRef, customer.id(), orderReq.products()));
        var pricedById = reserve.priced().stream()
                .collect(Collectors.toMap(ProductRes::id, p -> p));

        // 4) (pseudo) payment using total from priced response
        Map<Integer, Integer> qtyByProduct = orderReq.products().stream()
                .collect(Collectors.groupingBy(PurchaseRequest::productId, Collectors.summingInt(pr -> (int) pr.quantity())));

        BigDecimal total = reserve.priced().stream()
                .map(p -> p.finalUnitPrice().multiply(BigDecimal.valueOf(qtyByProduct.getOrDefault(p.id(), 0))))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // TODO : Add payment client here
        boolean paid = true; // replace with your real call


        if (!paid) {
            // ensure we release reservation if payment fails
            try {
                productClient.cancel(orderRef);
            } catch (Exception ignored) {
            }
            throw new BusinessException("Payment failed for orderRef=" + orderRef);
        }

        // 5) persist order + lines (DB only)
        Order order = orderMapper.toEntity(orderReq);
        order.setReference(orderRef);
        order.setCustomerId(orderReq.customerId());
        order.setPaymentMethod(orderReq.paymentMethod());
        order.setTotalAmount(total);

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

        // 6) after commit: CONFIRM the reservation (permanent decrement)
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                // DB committed → finalize inventory + publish event
                productClient.confirm(orderRef);
                orderEventPublisher.publishOrderCreated(saved,customer);
            }

            @Override
            public void afterCompletion(int status) {
                if (status != TransactionSynchronization.STATUS_COMMITTED) {
                    // rolled back → release reservation
                    try { productClient.cancel(orderRef); } catch (Exception ignored) {}
                }
            }
        });

        return saved.getId();
    }

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

