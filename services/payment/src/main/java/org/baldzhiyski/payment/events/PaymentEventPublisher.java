package org.baldzhiyski.payment.events;

import lombok.RequiredArgsConstructor;
import org.baldzhiyski.payment.payment.Payment;
import org.baldzhiyski.payment.payment.req.Customer;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange appEventsExchange;

    public void publishPaymentSucceeded(Payment payment, Customer customer) {
        publish(payment, customer, RoutingKey.SUCCEEDED);
    }

    public void publishPaymentFailed(Payment payment, Customer customer) {
        publish(payment, customer, RoutingKey.FAILED);
    }

    private void publish(Payment payment, Customer customer, RoutingKey rk) {
        var event = PaymentCreatedEvent.of(payment, customer);

        var correlation = new CorrelationData(event.eventId()); // useful for publisher confirms

        rabbitTemplate.convertAndSend(
                appEventsExchange.getName(),
                rk.value,
                event,
                msg -> {
                    msg.getMessageProperties().setMessageId(event.eventId());
                    msg.getMessageProperties().setHeader("orderRef", event.orderRef());
                    return msg;
                },
                correlation
        );
    }

    private enum RoutingKey {
        SUCCEEDED("payment.succeeded"),
        FAILED("payment.failed");
        final String value;
        RoutingKey(String value) { this.value = value; }
    }
}

