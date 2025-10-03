package org.baldzhiyski.mcpserverecommerce.client;

import org.baldzhiyski.mcpserverecommerce.response.ProductRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="product-client", url="${application.config.product-url}")
public interface ProductClient {
    @GetMapping
    ProductRes getAllProducts();

}
