package org.baldzhiyski.order.product;

import org.baldzhiyski.order.model.req.PurchaseRequest;

import java.util.List;

public record ReserveCommand(String orderRef, String customerId, List<PurchaseRequest> items) {}