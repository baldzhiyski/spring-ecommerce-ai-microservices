package org.baldzhiyski.product.model;

import lombok.Builder;

@Builder
public record JSONResponse(String message,String status) {

}