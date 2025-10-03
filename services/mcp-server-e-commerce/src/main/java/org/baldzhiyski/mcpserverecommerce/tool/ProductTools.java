package org.baldzhiyski.mcpserverecommerce.tool;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.baldzhiyski.mcpserverecommerce.client.ProductClient;
import org.baldzhiyski.mcpserverecommerce.response.ApiResponse;
import org.baldzhiyski.mcpserverecommerce.response.ProductRes;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductTools {

    private final ProductClient productClient;

    public ProductTools(ProductClient productClient) {
        this.productClient = productClient;
    }

    @Tool(name = "products-fetch-all", description = "Fetch all products from the Products service.")
    @CircuitBreaker(name = "productsClient", fallbackMethod = "productsFallback")
    public ApiResponse<ProductRes> fetchProducts() {
        ProductRes res = productClient.getAllProducts();
        return new ApiResponse<>("OK", "Fetched all products successfully", res);
    }

    // --- fallback ---
    public ApiResponse<ProductRes> productsFallback(Throwable t) {
        return new ApiResponse<>("ERROR", "Products service is unavailable", null);
    }
}

