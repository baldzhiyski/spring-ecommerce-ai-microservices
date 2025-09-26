package org.baldzhiyski.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Configuration
@EnableRabbit
public class RabbitConfig {


    @Bean
    public TopicExchange appEvents(@Value("${app.mq.exchange}") String ex) {
        return new TopicExchange(ex, true, false);
    }

    @Bean
    public Queue ordersQueue(@Value("${app.mq.queues.orders}") String q) {
        return QueueBuilder.durable(q).build();
    }

    @Bean
    public Queue paymentsQueue(@Value("${app.mq.queues.payments}") String q) {
        return QueueBuilder.durable(q).build();
    }

    @Bean
    public Queue usersQueue(@Value("${app.mq.queues.users}") String q) {
        return QueueBuilder.durable(q).build();
    }

    @Bean
    public Declarables bindings(TopicExchange appEvents,
                                Queue ordersQueue,
                                Queue paymentsQueue,
                                Queue usersQueue) {
        return new Declarables(
                BindingBuilder.bind(ordersQueue).to(appEvents).with("order.*"),
                BindingBuilder.bind(paymentsQueue).to(appEvents).with("payment.*"),
                BindingBuilder.bind(usersQueue).to(appEvents).with("customer.*")
        );
    }

    // Make RabbitTemplate send/receive JSON automatically
    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
