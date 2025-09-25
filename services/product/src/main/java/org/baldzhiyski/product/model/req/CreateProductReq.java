package org.baldzhiyski.product.model.req;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateProductReq(
        @NotBlank(message = "Name is required")
        @Size(max = 255, message = "Name must be at most 255 characters")
        String name,

        @Size(max = 255, message = "Description must be at most 255 characters")
        String description,

        @PositiveOrZero(message = "Available quantity must be zero or positive")
        double availableQuantity, // use Double if you want to allow null

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.00", inclusive = true, message = "Price must be at least 0.00")
        @Digits(integer = 36, fraction = 2, message = "Price must have up to 36 digits and 2 decimals")
        BigDecimal price,

        // optional; when provided must be between 0 and 1 (e.g., 0.15 = 15%)
        @DecimalMin(value = "0.000", inclusive = true, message = "Discount cannot be negative")
        @DecimalMax(value = "1.000", inclusive = true, message = "Discount cannot exceed 1.000 (100%)")
        @Digits(integer = 1, fraction = 3, message = "Discount must have up to 3 decimals (e.g., 0.150)")
        BigDecimal discount,

        @NotNull(message = "Category is required")
        @Valid
        CategoryReq categoryReq
) {}