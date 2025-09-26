package org.baldzhiyski.order.product;

import java.math.BigDecimal;


public record ProductRes(
         Integer id,
         String name,
         String description,
         BigDecimal price,
         BigDecimal discount,
         BigDecimal finalUnitPrice,
         Integer boughtQuantity
) {
}
