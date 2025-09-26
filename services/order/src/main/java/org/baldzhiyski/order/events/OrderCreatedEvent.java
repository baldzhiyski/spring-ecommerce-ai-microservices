package org.baldzhiyski.order.events;

import org.baldzhiyski.order.customer.CustomerRes;
import org.baldzhiyski.order.model.Order;
import org.baldzhiyski.order.model.OrderLine;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record OrderCreatedEvent(
        String eventId,
        String eventType,        // "order.created"
        String occurredAt,       // ISO-8601
        String orderRef,
        Integer orderId,
        String customerId,
        String customerName,
        String customerEmail,
        BigDecimal totalAmount
) {
    public static OrderCreatedEvent of(Order order, CustomerRes customer) {
        return new OrderCreatedEvent(
                UUID.randomUUID().toString(),
                "order.created",
                OffsetDateTime.now().toString(),
                order.getReference(),
                order.getId(),
                String.join(" ",customer.firstName(), customer.lastName()),
                customer.email(),
                order.getCustomerId(),
                order.getTotalAmount()
        );
    }
}
