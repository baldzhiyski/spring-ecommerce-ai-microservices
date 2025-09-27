package org.baldzhiyski.payment.controller;

import lombok.RequiredArgsConstructor;
import org.baldzhiyski.payment.config.StripeProps;
import org.baldzhiyski.payment.events.PaymentEventPublisher;
import org.baldzhiyski.payment.service.StripeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stripe/webhook")
@RequiredArgsConstructor
class StripeWebhookController {

    private final StripeProps stripe;
    private final StripeService service;

    @PostMapping
    public ResponseEntity<String> handle(@RequestHeader("Stripe-Signature") String sig,
                                         @RequestBody String payload) {
        try {
            var event = com.stripe.net.Webhook.constructEvent(payload, sig, stripe.webhookSecret());

            switch (event.getType()) {
                case "checkout.session.completed" -> {
                    var obj = event.getDataObjectDeserializer().getObject().orElse(null);
                    var session = (com.stripe.model.checkout.Session) obj;
                    if (session != null) service.handleCheckoutCompleted(session);
                }
                case "payment_intent.payment_failed" -> {
                    var obj = event.getDataObjectDeserializer().getObject().orElse(null);
                    var pi = (com.stripe.model.PaymentIntent) obj;
                    if (pi != null) service.handlePaymentFailed(pi);
                }
                default -> { /* ignore others */ }
            }
            return ResponseEntity.ok().build();
        } catch (com.stripe.exception.SignatureVerificationException e) {
            return ResponseEntity.badRequest().body("Invalid signature");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Webhook error");
        }
    }
}
