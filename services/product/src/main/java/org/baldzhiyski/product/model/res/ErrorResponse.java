package org.baldzhiyski.product.model.res;


import java.util.Map;

public record ErrorResponse(Map<String, String> errors) {
}
