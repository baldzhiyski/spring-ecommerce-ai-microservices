package org.baldzhiyski.product.model.req;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CustomerDiscountUpsertReq(
        @NotNull(message = "customerId is required") String customerId,
        @NotNull(message = "productId is required") Integer productId,
        @NotNull(message = "discount is required")
        @DecimalMin(value = "0.000", inclusive = true, message = "discount must be >= 0.000")
        @DecimalMax(value = "1.000", inclusive = true, message = "discount must be <= 1.000")
        @Digits(integer = 1, fraction = 3, message = "discount must be fraction 0..1 with up to 3 decimals")
        BigDecimal discount,
        OffsetDateTime startsAt,
        OffsetDateTime endsAt,
        Boolean active // optional; defaults to true if null
) {}