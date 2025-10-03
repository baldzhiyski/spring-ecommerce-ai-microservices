package org.baldzhiyski.payment.payment.res;

import org.baldzhiyski.payment.payment.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;


public record PaymentRes(
        Integer id,
        String paymentRef,
        PaymentStatus paymentStatus,
        BigDecimal amount,
        String orderRef,
        boolean successEmailSent,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

