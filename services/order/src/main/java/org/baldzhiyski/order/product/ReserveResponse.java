package org.baldzhiyski.order.product;

import org.baldzhiyski.order.model.req.PurchaseRequest;

import java.time.OffsetDateTime;
import java.util.List;

public record ReserveResponse(String orderRef, OffsetDateTime expiresAt, List<ProductRes> priced) {}