package org.baldzhiyski.order.model.req;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PurchaseRequest(
        @NotNull(message = "Product is mandatory") Integer productId,
        @Positive(message = "Quantity is mandatory") double quantity
) {
}
