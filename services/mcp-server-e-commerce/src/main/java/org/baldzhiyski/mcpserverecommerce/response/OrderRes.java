package org.baldzhiyski.mcpserverecommerce.response;

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