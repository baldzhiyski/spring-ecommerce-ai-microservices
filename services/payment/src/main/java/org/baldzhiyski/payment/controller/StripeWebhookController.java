package org.baldzhiyski.payment.controller;

import lombok.RequiredArgsConstructor;
import org.baldzhiyski.payment.config.StripeProps;
import org.baldzhiyski.payment.service.StripeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.net.Webhook;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/stripe/webhook")
@RequiredArgsConstructor
class StripeWebhookController {

    private final StripeProps stripe;
    private final StripeService service;
    private final ObjectMapper om = new ObjectMapper(); // or inject a Spring-managed ObjectMapper

    @PostMapping(consumes = "application/json")
    public ResponseEntity<String> handle(@RequestHeader("Stripe-Signature") String sig,
                                         @RequestBody String payload) {
        try {
            // Only verify signature; do NOT deserialize with Stripe SDK
            Webhook.Signature.verifyHeader(payload, sig, stripe.webhookSecret(), (long) 300);

            JsonNode root = om.readTree(payload);
            String type = text(root, "type");
            JsonNode obj = root.path("data").path("object");

            switch (type) {
                case "payment_intent.succeeded" -> handlePiSucceeded(obj);
                case "payment_intent.payment_failed" -> handlePiFailed(obj);
                case "checkout.session.completed" -> handleCheckoutCompleted(obj);
                case "charge.succeeded" -> handleChargeSucceeded(obj);
                default -> { /* ignore */ }
            }
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Webhook error");
        }
    }

    /* ------------ RAW handlers (no Stripe SDK models) ------------ */

    private void handlePiSucceeded(JsonNode pi) {
        String receiptUrl = text(pi.path("metadata"), "receiptUrl", null);
        String paymentMethod = text(pi.path("payment_method"), "payment_method", "UNKNOWN");
        String paymentRef = text(pi, "id");
        String orderRef   = text(pi.path("metadata"), "orderRef", "UNKNOWN");
        long amountMinor  = pi.path("amount").asLong(0);
        String currency   = text(pi, "currency");
        String email      = text(pi.path("metadata"), "customerEmail", null);
        String fullName   = text(pi.path("metadata"), "customerFullName", null);

        // Try latest_charge.billing_details as fallback for email/name
        if (isBlank(email)) {
            String latestChargeId = text(pi, "latest_charge", null);
            JsonNode charges = pi.path("charges").path("data");
            if (charges.isArray() && charges.size() > 0) {
                JsonNode bill = charges.get(0).path("billing_details");
                email    = or(text(bill, "email", null), email);
                fullName = or(text(bill, "name", null), fullName);
            }
        }

        service.handleSuccess(paymentRef, orderRef, amountMinor, or(currency, "eur"), fullName, email, receiptUrl, paymentMethod);
    }

    private void handlePiFailed(JsonNode pi) {
        String receiptUrl = text(pi.path("metadata"), "receiptUrl", null);
        String paymentMethod = text(pi.path("payment_method"), "payment_method", "UNKNOWN");
        String paymentRef = text(pi, "id");
        String orderRef   = text(pi.path("metadata"), "orderRef", "UNKNOWN");
        long amountMinor  = pi.path("amount").asLong(0);
        String currency   = text(pi, "currency", "eur");
        String email      = text(pi.path("metadata"), "customerEmail", null);
        String fullName   = text(pi.path("metadata"), "customerFullName", null);

        // Reason from last_payment_error / charge outcome
        String reason = null;
        JsonNode e = pi.path("last_payment_error");
        if (!e.isMissingNode() && !e.isNull()) {
            reason = join(" | ",
                    text(e, "message", null),
                    text(e, "code", null),
                    text(e, "decline_code", null));
        }
        if (isBlank(reason)) {
            JsonNode charges = pi.path("charges").path("data");
            if (charges.isArray() && charges.size() > 0) {
                JsonNode ch = charges.get(0);
                reason = join(" | ",
                        text(ch, "failure_message", null),
                        text(ch, "failure_code", null),
                        text(ch.path("outcome"), "reason", null));
                if (isBlank(email))    email    = text(ch.path("billing_details"), "email", null);
                if (isBlank(fullName)) fullName = text(ch.path("billing_details"), "name",  null);
            }
        }
        if (isBlank(reason)) reason = "payment_failed";

        service.handleFailure(paymentRef, orderRef, amountMinor, or(currency, "eur"), fullName, email, reason);
    }

    private void handleCheckoutCompleted(JsonNode session) {
        String receiptUrl = text(session.path("metadata"), "receiptUrl", null);
        String paymentMethod = text(session.path("payment_method"), "payment_method", "UNKNOWN");
        String paymentRef = text(session, "payment_intent", null);
        if (isBlank(paymentRef)) return;
        String orderRef   = text(session.path("metadata"), "orderRef", "UNKNOWN");
        long amountMinor  = session.path("amount_total").asLong(0);
        String currency   = text(session, "currency", "eur");
        String email      = text(session, "customer_email", null);
        String fullName   = text(session.path("metadata"), "customerFullName", null);

        service.handleSuccess(paymentRef, orderRef, amountMinor, currency, fullName, email, receiptUrl, paymentMethod);
    }

    private void handleChargeSucceeded(JsonNode charge) {
        String paymentRef = text(charge, "payment_intent", null);
        String orderRef   = text(charge.path("metadata"), "orderRef", "UNKNOWN");
        String receiptUrl = text(charge.path("metadata"), "receiptUrl", null);
        String paymentMethod = text(charge.path("payment_method"), "payment_method", "UNKNOWN");
        long amountMinor  = charge.path("amount").asLong(0);
        String currency   = text(charge, "currency", "eur");
        JsonNode bill     = charge.path("billing_details");
        String email      = text(bill, "email", null);
        String fullName   = text(bill, "name",  null);

        service.handleSuccess(paymentRef, orderRef, amountMinor, currency, fullName, email,receiptUrl,paymentMethod);
    }

    /* ------------ tiny utils ------------ */

    private static String text(JsonNode node, String field) {
        return text(node, field, null);
    }
    private static String text(JsonNode node, String field, String def) {
        if (node == null || node.isMissingNode() || node.isNull()) return def;
        JsonNode v = node.path(field);
        return (v.isMissingNode() || v.isNull()) ? def : v.asText();
    }
    private static String or(String v, String def) { return isBlank(v) ? def : v; }
    private static boolean isBlank(String s) { return s == null || s.isBlank(); }
    private static String join(String sep, String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (isBlank(p)) continue;
            if (!sb.isEmpty()) sb.append(sep);
            sb.append(p);
        }
        return sb.toString();
    }
}

