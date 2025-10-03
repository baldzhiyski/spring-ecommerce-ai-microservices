package org.baldzhiyski.payment.client;


import org.baldzhiyski.payment.product.ProductRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "products-client", url = "${application.config.product-url}")
public interface ProductsClient {
    // Controller should accept: ?ids=1&ids=2&ids=3
    @GetMapping("/all-by-id")
    List<ProductRes> getAllByIds(@RequestParam("ids") List<Integer> ids);

}