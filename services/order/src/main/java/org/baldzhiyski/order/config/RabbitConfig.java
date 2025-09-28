package org.baldzhiyski.order.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Bean
    TopicExchange appEventsExchange(@Value("${app.mq.exchange}") String name) {
        return new TopicExchange(name, true, false);
    }

    @Bean
    Queue paymentsQueue(@Value("${app.mq.queues.payments}") String name) {
        return QueueBuilder.durable(name).build();
    }

    @Bean
    Binding bindSucceeded(Queue paymentsQueue, TopicExchange appEventsExchange) {
        return BindingBuilder.bind(paymentsQueue).to(appEventsExchange).with("payment.succeeded");
    }

    @Bean
    Binding bindFailed(Queue paymentsQueue, TopicExchange appEventsExchange) {
        return BindingBuilder.bind(paymentsQueue).to(appEventsExchange).with("payment.failed");
    }

    @Bean
    public MessageConverter jackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf, MessageConverter mc) {
        RabbitTemplate rt = new RabbitTemplate(cf);
        rt.setMessageConverter(mc);
        return rt;
    }
}

