package org.baldzhiyski.payment.payment.req;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.baldzhiyski.payment.payment.PaymentMethod;

import java.math.BigDecimal;


public record PaymentReq(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        @Valid @NotNull Customer customer
) {}
