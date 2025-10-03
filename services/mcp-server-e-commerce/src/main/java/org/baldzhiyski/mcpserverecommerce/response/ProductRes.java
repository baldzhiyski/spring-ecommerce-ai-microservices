package org.baldzhiyski.mcpserverecommerce.response;

import java.math.BigDecimal;

public record ProductRes(String id, String name, String description, BigDecimal price, Category category) {
}
