package org.baldzhiyski.order.model.res;

import org.baldzhiyski.order.model.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderRes(
        Integer id,
        String reference,
        String customerId,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<OrderLineRes> lines
) {}