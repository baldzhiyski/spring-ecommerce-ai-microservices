package org.baldzhiyski.product.model.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BuyProductReq(

        @NotNull(message = "Product ID is required")
        @Min(value = 1, message = "Product ID must be a positive integer")
        Integer id,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity
) {}