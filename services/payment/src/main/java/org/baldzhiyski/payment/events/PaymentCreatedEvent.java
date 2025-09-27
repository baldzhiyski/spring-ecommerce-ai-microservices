package org.baldzhiyski.payment.events;

import org.baldzhiyski.payment.payment.Payment;
import org.baldzhiyski.payment.payment.req.Customer;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PaymentCreatedEvent(
        String eventId,
        String occurredAt,
        Integer orderId,
        String orderRef,
        String customerId,
        String customerName,
        String customerEmail,
        BigDecimal totalAmount
) {
    public static PaymentCreatedEvent of(Payment payment, Customer customer) {
        return new PaymentCreatedEvent(
                UUID.randomUUID().toString(),
                OffsetDateTime.now().toString(),
                payment.getOrderId(),
                payment.getOrderRef(),
                customer.id(),
                String.join(" ", customer.firstName(), customer.lastName()),
                customer.email(),
                payment.getAmount()
        );
    }
}
