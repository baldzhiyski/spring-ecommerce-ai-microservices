package org.baldzhiyski.order.events;

import lombok.RequiredArgsConstructor;
import org.baldzhiyski.order.customer.CustomerRes;
import org.baldzhiyski.order.model.Order;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange ordersExchange;

    public void publishOrderCreated(Order order, CustomerRes customer) {
        var event = OrderCreatedEvent.of(order,customer);
        rabbitTemplate.convertAndSend(
                ordersExchange.getName(),
                "order.created",
                event,
                msg -> {
                    msg.getMessageProperties().setContentType(MessageProperties.CONTENT_TYPE_JSON);
                    msg.getMessageProperties().setMessageId(event.eventId());
                    msg.getMessageProperties().setCorrelationId(event.orderRef());
                    return msg;
                }
        );
    }
}
