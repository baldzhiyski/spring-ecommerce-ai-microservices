package org.baldzhiyski.payment.controller;

import com.stripe.param.checkout.SessionCreateParams;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.baldzhiyski.payment.client.OrdersClient;
import org.baldzhiyski.payment.client.ProductsClient;
import org.baldzhiyski.payment.config.StripeProps;
import org.baldzhiyski.payment.payment.req.PaymentReq;
import org.baldzhiyski.payment.product.ProductInfo;
import org.baldzhiyski.payment.product.ProductRes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
class StripeCheckoutController {

    private final StripeProps stripe;
    private final OrdersClient ordersClient;
    private final ProductsClient productsClient;

    @PostMapping("/checkout-session")
    public Map<String,Object> createSession(@RequestBody @Valid PaymentReq req) throws Exception {
        com.stripe.Stripe.apiKey = stripe.secretKey();

        // 1) get product infos (productId + quantity) from Orders
        List<ProductInfo> infos = ordersClient.findAllProductsInfoByOrderId(req.orderId());
        if (infos == null || infos.isEmpty()) {
            throw new IllegalStateException("No products for orderId=" + req.orderId());
        }

        // 2) fetch product details (name/description/price) from Products
        List<Integer> ids = infos.stream().map(ProductInfo::getProductId).distinct().toList();
        List<ProductRes> products = productsClient.getAllByIds(ids);

        // map by productId (ProductRes.id is String in your DTO)
        Map<Integer, ProductRes> productById = products.stream()
                .collect(Collectors.toMap(p -> Integer.valueOf(p.id()), p -> p));

        // 3) build Stripe line items
        List<SessionCreateParams.LineItem> stripeItems = new ArrayList<>();
        for (ProductInfo info : infos) {
            ProductRes p = productById.get(info.getProductId());
            if (p == null) {
                throw new IllegalStateException("Product not found for id=" + info.getProductId());
            }

            long unitAmountMinor = toMinorUnits(p.price(), stripe.currency()); // price from ProductRes
            long qty = Math.max(1, (long)Math.floor(info.getQuantity()));      // quantity from ProductInfo

            SessionCreateParams.LineItem item = SessionCreateParams.LineItem.builder()
                    .setQuantity(qty)
                    .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency(stripe.currency())
                                    .setUnitAmount(unitAmountMinor)
                                    .setProductData(
                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                    .setName(p.name())                  // show in Checkout/receipt
                                                    .setDescription(p.description())    // shows in Dashboard/receipt context
                                                    .putMetadata("productId", p.id())
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            stripeItems.add(item);
        }

        // 4) create session (keep your metadata)
        String customerFullName = req.customer().firstName() + " " + req.customer().lastName();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(stripe.successUrl() + "?orderRef=" + req.orderReference())
                .setCancelUrl(stripe.cancelUrl()  + "?orderRef=" + req.orderReference())
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setCustomerEmail(req.customer().email())
                .addAllLineItem(stripeItems)
                .setClientReferenceId(req.orderReference())
                .putMetadata("orderRef", req.orderReference())
                .putMetadata("customerId", req.customer().id())
                .putMetadata("customerFullName", customerFullName)
                .putMetadata("customerEmail", req.customer().email())
                .setPaymentIntentData(
                        SessionCreateParams.PaymentIntentData.builder()
                                .putMetadata("orderRef", req.orderReference())
                                .putMetadata("customerId", req.customer().id())
                                .putMetadata("paymentMethod", req.paymentMethod().toString())
                                .putMetadata("customerFullName", customerFullName)
                                .putMetadata("customerEmail", req.customer().email())
                                .build()
                )
                .build();

        var session = com.stripe.model.checkout.Session.create(params);
        return Map.of(
                "sessionId", session.getId(),
                "url", session.getUrl(),
                "orderRef", req.orderReference()
        );
    }

    private long toMinorUnits(BigDecimal amount, String currency) {
        int scale = switch (currency.toUpperCase(Locale.ROOT)) {
            case "JPY" -> 0; default -> 2;
        };
        return amount.setScale(scale, RoundingMode.HALF_UP)
                .movePointRight(scale)
                .longValueExact();
    }

    @GetMapping("/demo/success")
    public ResponseEntity<Map<String, Object>> success(@RequestParam String orderRef) {
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Payment confirmed",
                "orderRef", orderRef,
                "method", "stripe"
        ));
    }

    @GetMapping("/demo/cancel")
    public ResponseEntity<Map<String, Object>> cancel(@RequestParam String orderRef) {
        return ResponseEntity.ok(Map.of(
                "status", "cancelled",
                "message", "Payment was not completed",
                "orderRef", orderRef,
                "method", "stripe"
        ));
    }

}
