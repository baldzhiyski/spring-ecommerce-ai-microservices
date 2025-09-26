package org.baldzhiyski.order.model.res;

public record OrderLineRes(
        Integer id,
        Integer productId,
        double quantity
) {}