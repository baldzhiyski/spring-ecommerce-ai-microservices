package org.baldzhiyski.order.model.req;


import org.baldzhiyski.order.model.PaymentMethod;

import java.math.BigDecimal;

public record PaymentCheckoutReq(
        String orderReference,
        PaymentMethod paymentMethod,
        Integer orderId,
        Customer customer,
        BigDecimal amount
) {
    public record Customer(
            String id,
            String email,
            String firstName,
            String lastName
    ) {}
}
