package org.baldzhiyski.mcpserverecommerce.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.baldzhiyski.mcpserverecommerce.client.OrderClient;
import org.baldzhiyski.mcpserverecommerce.client.ProductClient;
import org.baldzhiyski.mcpserverecommerce.response.OrderRes;
import org.baldzhiyski.mcpserverecommerce.response.ProductRes;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class McpService {

    private final OrderClient orderClient;
    private final ProductClient productClient;

    public McpService(OrderClient orderClient, ProductClient productClient) {
        this.orderClient = orderClient;
        this.productClient = productClient;
    }

    // --- Feign calls with circuit breakers ---
    @CircuitBreaker(name = "ordersClient", fallbackMethod = "ordersFallback")
    public List<OrderRes> fetchOrders() {
        return orderClient.getAllOrders();
    }

    @CircuitBreaker(name = "ordersClient", fallbackMethod = "ordersByCustomerFallback")
    public List<OrderRes> fetchOrdersByCustomer(String customerId) {
        return orderClient.getAllByCustomerId(customerId);
    }

    @CircuitBreaker(name = "productsClient", fallbackMethod = "productsFallback")
    public List<ProductRes> fetchProducts() {
        return productClient.getAllProducts();
    }

    // --- Fallbacks with dummy responses ---
    public OrderRes ordersFallback(Throwable t) {
        return new OrderRes(
                -1,                          // dummy id
                "UNAVAILABLE",               // dummy reference
                "N/A",                       // no customer
                BigDecimal.ZERO,             // no amount
                null,                        // no payment method
                LocalDateTime.now(),
                LocalDateTime.now(),
                Collections.emptyList()      // no order lines
        );
    }

    public OrderRes ordersByCustomerFallback(String customerId, Throwable t) {
        return new OrderRes(
                -1,
                "UNAVAILABLE",
                customerId,                  // keep the customerId for context
                BigDecimal.ZERO,
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                Collections.emptyList()
        );
    }

    public ProductRes productsFallback(Throwable t) {
        return new ProductRes(
                "UNAVAILABLE",               // dummy id
                "Products service unavailable", // name as message
                "",                          // no description
                BigDecimal.ZERO,             // no price
                null                         // no category
        );
    }
}
