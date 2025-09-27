package org.baldzhiyski.order.model.res;

import java.util.Map;

public record ErrorResponse(Map<String, String> errors) {
}
