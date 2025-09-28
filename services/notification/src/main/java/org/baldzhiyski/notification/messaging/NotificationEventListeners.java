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
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListeners {

    private final TemplatedEmailService templated;

    @RabbitListener(queues = "${app.mq.queues.orders}", concurrency = "2-8")
    public void onOrderEvents(Message message, Map<String, Object> payload) {
        String rk = message.getMessageProperties().getReceivedRoutingKey();

        String to = text(payload, "customerEmail");
        if (to == null || to.isBlank()) {
            log.warn("Missing email for {}", rk);
            return;
        }

        Map<String, Object> model = new HashMap<>();
        model.put("orderRef", text(payload, "orderRef"));
        model.put("orderId", payload.getOrDefault("orderId", 0));
        model.put("customerName", text(payload, "customerName"));
        model.put("totalAmount", text(payload, "totalAmount"));
        model.put("brand", "Baldzhiyski Shop");

        templated.send(to, rk, model);
    }
    @RabbitListener(queues = "${app.mq.queues.payments}", concurrency = "3-12")
    public void onPaymentEvents(Message message, Map<String, Object> payload) {
        String rk = message.getMessageProperties().getReceivedRoutingKey(); // "payment.succeeded" | "payment.failed"

        String to = text(payload, "customerEmail");
        if (to == null || to.isBlank()) {
            log.warn("Missing email for {}", rk);
            return;
        }

        final String brand      = "Baldzhiyski Shop";
        final String orderRef   = text(payload, "orderRef");
        final String paymentRef = text(payload, "paymentRef");
        final String fullName   = text(payload, "customerFullName");
        final String currency   = text(payload, "currency");   // "eur" etc.
        final String method     = text(payload, "method");     // "stripe"
        final String receiptUrl = text(payload,"receiptUrl") ;
        final boolean isSuccess = "payment.succeeded".equals(rk);

        String amountDisplay = formatAmountDecimal(payload.get("amount"), currency);

        String subject = (isSuccess ? "Payment received" : "Payment failed")
                + " • " + brand
                + (orderRef != null ? " • " + orderRef : "");

        Map<String, Object> model = new HashMap<>();
        model.put("subject", subject);
        model.put("brand", brand);
        model.put("customerName", fullName);
        model.put("orderRef", orderRef);
        model.put("paymentRef", paymentRef);
        model.put("amount", amountDisplay);
        if (method != null && !method.isBlank()) model.put("method", method);

        if (isSuccess) {
            if (orderRef != null) {
                model.put("ctaUrl",receiptUrl);
                model.put("ctaText", "View your order");
            }
        } else {
            String reason = text(payload, "failureReason");
            if (reason != null && !reason.isBlank()) model.put("failureReason", reason);
            if (orderRef != null) {
                model.put("ctaUrl",receiptUrl );
                model.put("ctaText", "Try again");
            }
        }

        templated.send(to, rk, model);
    }

    /* ---------- helpers for Map payload ---------- */

    private static String text(Map<String, Object> map, String key) {
        if (map == null) return null;
        Object v = map.get(key);
        if (v == null) return null;
        return String.valueOf(v);
    }

    private static String formatAmountDecimal(Object amountObj, String currency) {
        if (amountObj == null) return "";
        BigDecimal dec = toBigDecimal(amountObj);
        String iso = (currency == null ? "EUR" : currency).toUpperCase(Locale.ROOT);
        String symbol = switch (iso) {
            case "EUR" -> "€";
            case "USD" -> "$";
            case "GBP" -> "£";
            default -> "";
        };
        return symbol.isEmpty() ? dec + " " + iso : symbol + dec;
    }

    private static BigDecimal toBigDecimal(Object v) {
        if (v instanceof BigDecimal bd) return bd;
        if (v instanceof Number n) return new BigDecimal(n.toString());
        String s = String.valueOf(v).trim();
        if (s.isEmpty()) return BigDecimal.ZERO;
        return new BigDecimal(s);
    }

}
