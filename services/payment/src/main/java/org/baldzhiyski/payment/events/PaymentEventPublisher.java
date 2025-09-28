package org.baldzhiyski.payment.events;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {
    private final RabbitTemplate rabbit;
    private final TopicExchange appEventsExchange;

    public void paymentSucceeded(String orderRef, String paymentRef, long amountMinor,
                                 String currency, String fullName, String email, String receiptUrl, String paymentMethod) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventId", UUID.randomUUID().toString());
        event.put("eventType", "payment.succeeded");
        event.put("occurredAt", OffsetDateTime.now().toString());
        event.put("orderRef", orderRef);
        event.put("paymentRef", paymentRef);
        event.put("amount", BigDecimal.valueOf(amountMinor).movePointLeft(2));
        putIfNotBlank(event, "currency", currency);
        putIfNotBlank(event, "customerFullName", fullName);
        putIfNotBlank(event, "customerEmail", email);
        putIfNotBlank(event, "receiptUrl", receiptUrl);
        putIfNotBlank(event, "paymentMethod", paymentMethod);
        event.put("method", "stripe");
        rabbit.convertAndSend(appEventsExchange.getName(), "payment.succeeded", event);
    }
    private static void putIfNotBlank(Map<String, Object> m, String key, String value) {
        if (value != null && !value.isBlank()) m.put(key, value);
    }


    public void paymentFailed(
            String orderRef,
            String paymentRef,
            long amountMinor,
            String currency,
            String fullName,
             String email,
           String reason
    ) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventId", UUID.randomUUID().toString());
        event.put("eventType", "payment.failed");
        event.put("occurredAt", OffsetDateTime.now().toString());
        event.put("orderRef", orderRef);
        event.put("paymentRef", paymentRef);
        event.put("amount", BigDecimal.valueOf(amountMinor).movePointLeft(2));
        event.put("currency", currency);
        if (fullName != null && !fullName.isBlank()) event.put("customerFullName", fullName);
        if (email != null && !email.isBlank())       event.put("customerEmail", email);
        if (reason != null && !reason.isBlank())     event.put("failureReason", reason);
        event.put("method", "stripe");

        rabbit.convertAndSend(appEventsExchange.getName(), "payment.failed", event);
    }

}
