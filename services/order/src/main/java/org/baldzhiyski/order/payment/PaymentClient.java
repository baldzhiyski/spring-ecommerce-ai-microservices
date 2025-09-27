package org.baldzhiyski.order.payment;

import org.baldzhiyski.order.model.req.PaymentCheckoutReq;
import org.baldzhiyski.order.model.res.PaymentCheckoutRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "payment-service",
        url = "${application.config.payment-url}" //
)
public interface PaymentClient {
    @PostMapping("/checkout-session")
    PaymentCheckoutRes createCheckoutSession(@RequestBody PaymentCheckoutReq req);
}