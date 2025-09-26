package org.baldzhiyski.notification.service.impl;

import org.baldzhiyski.notification.service.TemplateRegistry;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DefaultTemplateRegistry implements TemplateRegistry {

    private static final Map<String, String> MAP = Map.ofEntries(
            Map.entry("order.created",   "order-created"),
            Map.entry("order.shipped",   "order-shipped"),
            Map.entry("payment.succeeded","payment-succeeded"),
            Map.entry("payment.failed",  "payment-failed")
    );

    @Override
    public String templateFor(String key) {
        return MAP.getOrDefault(key, "order-created"); // fallback
    }

    @Override
    public String subjectFor(String key, Map<String, Object> model) {
        return switch (key) {
            case "order.created"    -> "Your order " + model.getOrDefault("orderRef", "") + " is confirmed";
            case "payment.succeeded"-> "Payment succeeded";
            case "payment.failed"   -> "Payment failed";
            default -> "Notification";
        };
    }
}
