package org.baldzhiyski.customer.dto.res;

import java.util.Map;

public record ErrorResponse(Map<String, String> errors) {
}
