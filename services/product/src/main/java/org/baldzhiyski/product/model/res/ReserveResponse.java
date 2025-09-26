package org.baldzhiyski.product.model.res;

import java.time.OffsetDateTime;
import java.util.List;

public record ReserveResponse(String orderRef, OffsetDateTime expiresAt, List<ProductPurchasedResp> priced) {}