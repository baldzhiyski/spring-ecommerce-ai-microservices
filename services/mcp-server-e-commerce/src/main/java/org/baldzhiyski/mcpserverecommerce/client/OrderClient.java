package org.baldzhiyski.mcpserverecommerce.client;

import org.baldzhiyski.mcpserverecommerce.response.OrderRes;
import org.baldzhiyski.mcpserverecommerce.response.ProductRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="order-client", url="${application.config.order-url}")
public interface OrderClient {
    @GetMapping
    List<OrderRes> getAllOrders();

    @GetMapping
    List<OrderRes> getAllByCustomerId(@RequestParam String customerId);

}