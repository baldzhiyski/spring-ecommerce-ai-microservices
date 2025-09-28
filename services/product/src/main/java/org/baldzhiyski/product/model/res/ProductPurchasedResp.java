package org.baldzhiyski.product.model.res;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductPurchasedResp  {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discount;
    BigDecimal finalUnitPrice;
    private Integer boughtQuantity;
}
