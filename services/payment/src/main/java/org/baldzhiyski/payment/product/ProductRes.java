package org.baldzhiyski.payment.product;


import java.math.BigDecimal;

public record ProductRes(String id,String name, String description, BigDecimal price) {
}
