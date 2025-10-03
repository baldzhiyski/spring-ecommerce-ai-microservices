package org.baldzhiyski.payment.client;


import org.baldzhiyski.payment.product.ProductInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "orders-client", url = "${application.config.order-url}")
public interface OrdersClient {
    @GetMapping("/product-info/{orderId}")
    List<ProductInfo> findAllProductsInfoByOrderId(@PathVariable("orderId") Integer id);
}
