package org.baldzhiyski.product.model.req;

import java.util.List;

public record ReserveCommand(String orderRef, String customerId, List<BuyProductReq> items) {}