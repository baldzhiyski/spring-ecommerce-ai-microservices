package org.baldzhiyski.notification.messaging;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baldzhiyski.notification.email.EmailSender;
import org.baldzhiyski.notification.email.TemplatedEmailService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

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
        String rk = message.getMessageProperties().getReceivedRoutingKey();
        String to = payload.path("customerEmail").asText(null);
        if (to == null || to.isBlank()) { log.warn("Missing email for {}", rk); return; }

        Map<String,Object> model = Map.of(
                "paymentRef", payload.path("paymentRef").asText(""),
                "amount",     payload.path("amount").asText(""),
                "status",     rk.equals("payment.succeeded") ? "Succeeded" : "Failed",
                "brand",      "Baldzhiyski Shop"
        );

        templated.send(to, rk, model);
    }

}
