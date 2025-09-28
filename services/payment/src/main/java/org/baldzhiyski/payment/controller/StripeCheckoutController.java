package org.baldzhiyski.payment.controller;

import com.stripe.param.checkout.SessionCreateParams;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.baldzhiyski.payment.config.StripeProps;
import org.baldzhiyski.payment.payment.req.PaymentReq;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
class StripeCheckoutController {

    private final StripeProps stripe;

    @PostMapping("/checkout-session")
    public Map<String,Object> createSession(@RequestBody @Valid PaymentReq req) throws Exception {
        com.stripe.Stripe.apiKey = stripe.secretKey();

        String customerFullName = String.join(" ", req.customer().firstName(), req.customer().lastName());
        var params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(stripe.successUrl() + "?orderRef=" + req.orderReference())
                .setCancelUrl(stripe.cancelUrl()  + "?orderRef=" + req.orderReference())
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setCustomerEmail(req.customer().email())
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency(stripe.currency())
                                        .setUnitAmount(req.amount().longValue())
                                        .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                .setName("Order " + req.orderReference())
                                                .build())
                                        .build())
                                .build())
                // metadata on the Session (optional)
                .putMetadata("orderRef", req.orderReference())
                .putMetadata("customerId", req.customer().id())
                .putMetadata("customerFullName", customerFullName)
                .putMetadata("customerEmail", req.customer().email())
                // ✅ metadata on the PaymentIntent created by Checkout
                .setPaymentIntentData(
                        SessionCreateParams.PaymentIntentData.builder()
                                .putMetadata("orderRef", req.orderReference())
                                .putMetadata("customerId", req.customer().id())
                                .putMetadata("paymentMethod",req.paymentMethod().toString())
                                .putMetadata("customerFullName", customerFullName)
                                .putMetadata("customerEmail", req.customer().email())
                                .build()
                )
                .build();


        var session = com.stripe.model.checkout.Session.create(params);
        return Map.of(
                "sessionId", session.getId(),
                "url", session.getUrl(),                // open in browser manually
                "orderRef", req.orderReference()
        );
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
