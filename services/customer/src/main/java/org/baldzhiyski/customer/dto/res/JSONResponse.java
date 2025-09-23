package org.baldzhiyski.customer.dto.res;

import lombok.Builder;

@Builder
public record JSONResponse(String message,String status) {

}
