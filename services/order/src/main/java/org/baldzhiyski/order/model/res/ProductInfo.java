package org.baldzhiyski.order.model.res;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductInfo {
    private Integer productId;
    private double quantity;
}
