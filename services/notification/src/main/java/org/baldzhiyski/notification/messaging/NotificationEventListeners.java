package org.baldzhiyski.notification.messaging;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baldzhiyski.notification.email.EmailSender;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationEventListeners {

    private final EmailSender email;

    // ----- ORDER EVENTS -----
    @RabbitListener(queues = "${app.mq.queues.orders}", concurrency = "2-8")
    public void onOrderEvents(Message message, JsonNode payload) {
        final String rk = message.getMessageProperties().getReceivedRoutingKey();
        final String to = payload.path("email").asText(null);
        if (to == null || to.isBlank()) {
            log.warn("Order event without email, rk={}, payload={}", rk, payload);
            return;
        }
        switch (rk) {
            case "order.created" -> email.send(to, "Order created", payload.toPrettyString());
            case "order.paid"    -> email.send(to, "Order paid",    payload.toPrettyString());
            case "order.shipped" -> email.send(to, "Order shipped", payload.toPrettyString());
            default -> log.debug("Unhandled order rk={}, payload={}", rk, payload);
        }
    }

    // ----- PAYMENT EVENTS -----
    @RabbitListener(queues = "${app.mq.queues.payments}", concurrency = "3-12")
    public void onPaymentEvents(Message message, JsonNode payload) {
        final String rk = message.getMessageProperties().getReceivedRoutingKey();
        final String to = payload.path("email").asText(null);
        if (to == null || to.isBlank()) {
            log.warn("Payment event without email, rk={}, payload={}", rk, payload);
            return;
        }
        switch (rk) {
            case "payment.succeeded" -> email.send(to, "Payment succeeded", payload.toPrettyString());
            case "payment.failed"    -> email.send(to, "Payment failed",    payload.toPrettyString());
            case "refund.created"    -> email.send(to, "Refund created",    payload.toPrettyString());
            default -> log.debug("Unhandled payment rk={}, payload={}", rk, payload);
        }
    }

    // ----- USER EVENTS -----
    @RabbitListener(queues = "${app.mq.queues.users}", concurrency = "1-4")
    public void onUserEvents(Message message, JsonNode payload) {
        final String rk = message.getMessageProperties().getReceivedRoutingKey();
        final String to = payload.path("email").asText(null);
        if (to == null || to.isBlank()) {
            log.warn("User event without email, rk={}, payload={}", rk, payload);
            return;
        }
        switch (rk) {
            case "user.registered"     -> email.send(to, "Welcome!",       payload.toPrettyString());
            case "user.password.reset" -> email.send(to, "Password reset", payload.toPrettyString());
            default -> log.debug("Unhandled user rk={}, payload={}", rk, payload);
        }
    }
}
