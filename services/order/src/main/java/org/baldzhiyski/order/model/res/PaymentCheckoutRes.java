package org.baldzhiyski.order.model.res;

public record PaymentCheckoutRes(
        String sessionId,
        String url,
        String orderRef
) {}