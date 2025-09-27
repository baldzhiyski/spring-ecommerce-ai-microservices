package org.baldzhiyski.notification.messaging;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baldzhiyski.notification.email.EmailSender;
import org.baldzhiyski.notification.email.TemplatedEmailService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListeners {

    private final TemplatedEmailService templated;

    @RabbitListener(queues = "${app.mq.queues.orders}", concurrency = "2-8")
    public void onOrderEvents(Message message, JsonNode payload) {
        String rk = message.getMessageProperties().getReceivedRoutingKey();
        String to = payload.path("customerEmail").asText(null);
        if (to == null || to.isBlank()) { log.warn("Missing email for {}", rk); return; }

        Map<String,Object> model = Map.of(
                "orderRef", payload.path("orderRef").asText(""),
                "orderId",  payload.path("orderId").asInt(0),
                "customerName", payload.path("customerName").asText(""),
                "totalAmount", payload.path("totalAmount").asText(""),
                "brand", "Baldzhiyski Shop"
        );

        templated.send(to, rk, model);
    }
    @RabbitListener(queues = "${app.mq.queues.payments}", concurrency = "3-12")
    public void onPaymentEvents(Message message, JsonNode payload) {
        String rk = message.getMessageProperties().getReceivedRoutingKey(); // "payment.succeeded" | "payment.failed"

        String to = text(payload, "customerEmail");
        if (to == null || to.isBlank()) {
            log.warn("Missing email for {}", rk);
            return;
        }

        final String brand       = "Baldzhiyski Shop";
        final String orderRef    = text(payload, "orderRef");
        final String paymentRef  = text(payload, "paymentRef");
        final String fullName    = text(payload, "customerFullName"); // publisher uses this key
        final String currency    = text(payload, "currency");         // e.g., "EUR"
        final String method      = text(payload, "method");           // "stripe"
        final boolean isSuccess  = "payment.succeeded".equals(rk);

        // Your publisher sends "amount" already as a decimal (e.g., 19.99) — format for display with currency.
        String amountDisplay = formatAmountDecimal(payload.get("amount"), currency);

        String subject = (isSuccess ? "Payment received" : "Payment failed")
                + " • " + brand
                + (orderRef != null ? " • " + orderRef : "");

        Map<String, Object> model = new HashMap<>();
        model.put("subject", subject);
        model.put("brand", brand);
        model.put("customerName", fullName);   // your template expects 'customerName'
        model.put("orderRef", orderRef);
        model.put("paymentRef", paymentRef);
        model.put("amount", amountDisplay);
        if (method != null && !method.isBlank()) model.put("method", method);

        if (isSuccess) {
            // Optional CTA to view order
            if (orderRef != null) {
                model.put("ctaUrl", "https://your-frontend/orders/" + orderRef);
                model.put("ctaText", "View your order");
            }
        } else {
            // Failed-specific fields
            String reason = text(payload, "failureReason");
            if (reason != null && !reason.isBlank()) model.put("failureReason", reason);
            if (orderRef != null) {
                model.put("ctaUrl", "https://your-frontend/checkout/retry?orderRef=" + orderRef);
                model.put("ctaText", "Try again");
            }
        }

        templated.send(to, rk, model);
    }

    private static String text(JsonNode node, String field) {
        JsonNode v = (node == null) ? null : node.get(field);
        return (v == null || v.isNull()) ? null : v.asText();
    }

    /**
     * Formats your published 'amount' (already decimal) with a simple currency symbol.
     * If 'amount' is missing, returns empty string.
     */
    private static String formatAmountDecimal(JsonNode amountNode, String currency) {
        if (amountNode == null || amountNode.isNull()) return "";
        String iso = (currency == null) ? "EUR" : currency.toUpperCase();

        // Read as BigDecimal regardless of JSON numeric/textual
        BigDecimal dec;
        if (amountNode.isNumber()) dec = amountNode.decimalValue();
        else dec = new BigDecimal(amountNode.asText("0"));

        String symbol = switch (iso) {
            case "EUR" -> "€";
            case "USD" -> "$";
            case "GBP" -> "£";
            default -> "";
        };
        return symbol.isEmpty() ? dec + " " + iso : symbol + dec;
    }


}
