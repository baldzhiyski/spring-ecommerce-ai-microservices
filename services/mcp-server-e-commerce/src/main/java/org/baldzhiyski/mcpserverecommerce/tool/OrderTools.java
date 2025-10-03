package org.baldzhiyski.mcpserverecommerce.tool;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.baldzhiyski.mcpserverecommerce.client.OrderClient;
import org.baldzhiyski.mcpserverecommerce.response.ApiResponse;
import org.baldzhiyski.mcpserverecommerce.response.OrderRes;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;


@Component
public class OrderTools {

    private final OrderClient orderClient;

    public OrderTools(OrderClient orderClient) {
        this.orderClient = orderClient;
    }

    @Tool(name = "orders-fetch-all", description = "Fetch all orders from the Orders service.")
    @CircuitBreaker(name = "ordersClient", fallbackMethod = "ordersFallback")
    public ApiResponse<OrderRes> fetchOrders() {
        OrderRes res = orderClient.getAllOrders();
        return new ApiResponse<>("OK", "Fetched all orders successfully", res);
    }

    @Tool(name = "orders-fetch-by-customer", description = "Fetch orders by a given customer ID.")
    @CircuitBreaker(name = "ordersClient", fallbackMethod = "ordersByCustomerFallback")
    public ApiResponse<OrderRes> fetchOrdersByCustomer(String customerId) {
        OrderRes res = orderClient.getAllByCustomerId(customerId);
        return new ApiResponse<>("OK", "Fetched orders for customer " + customerId, res);
    }

    // --- fallbacks ---
    public ApiResponse<OrderRes> ordersFallback(Throwable t) {
        return new ApiResponse<>("ERROR", "Orders service is unavailable", null);
    }

    public ApiResponse<OrderRes> ordersByCustomerFallback(String customerId, Throwable t) {
        if (t instanceof feign.FeignException.NotFound) {
            return new ApiResponse<>("NOT_FOUND", "No order found for customer " + customerId, null);
        }
        return new ApiResponse<>("ERROR", "Orders service unavailable for customer " + customerId, null);
    }
}


