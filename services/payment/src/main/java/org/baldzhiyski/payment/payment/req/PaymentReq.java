package org.baldzhiyski.payment.payment.req;

import jakarta.validation.Valid;
import org.baldzhiyski.payment.payment.PaymentMethod;

import java.math.BigDecimal;


public record PaymentReq(
        Integer id,
        BigDecimal amount,
        PaymentMethod paymentMethod ,
        Integer orderId,
        String orderReference,
       @Valid Customer customer
) {
}
