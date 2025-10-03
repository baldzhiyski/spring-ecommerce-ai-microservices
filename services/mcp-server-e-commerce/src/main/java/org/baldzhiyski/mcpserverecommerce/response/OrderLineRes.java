package org.baldzhiyski.mcpserverecommerce.response;

public record OrderLineRes(
        Integer id,
        Integer productId,
        double quantity
) {}