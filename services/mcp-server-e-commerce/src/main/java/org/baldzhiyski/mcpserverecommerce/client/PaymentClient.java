package org.baldzhiyski.mcpserverecommerce.client;


import org.baldzhiyski.mcpserverecommerce.response.OrderRes;
import org.baldzhiyski.mcpserverecommerce.response.PaymentRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="payment-client", url="${application.config.payment-url}")
public interface PaymentClient {

    @GetMapping
    PaymentRes getPaymentForCurrentOrderRef(@RequestParam String orderRef);
}
