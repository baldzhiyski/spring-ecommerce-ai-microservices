package org.baldzhiyski.product.model.res;

import org.baldzhiyski.product.model.Category;

import java.math.BigDecimal;

public record ProductRes(String id,String name, String description, BigDecimal price, CategoryRes category) {
}
