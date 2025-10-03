package org.baldzhiyski.mcpserverecommerce.tool;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.baldzhiyski.mcpserverecommerce.client.PaymentClient;
import org.baldzhiyski.mcpserverecommerce.response.ApiResponse;
import org.baldzhiyski.mcpserverecommerce.response.PaymentRes;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class PaymentTools {

    private final PaymentClient paymentClient;

    public PaymentTools(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    @Tool(
            name = "payment-by-order-ref",
            description = "Fetch payment details for a given orderRef."
    )
    @CircuitBreaker(name = "paymentsClient", fallbackMethod = "paymentByOrderRefFallback")
    public ApiResponse<PaymentRes> getPaymentByOrderRef(String orderRef) {
        PaymentRes res = paymentClient.getPaymentForCurrentOrderRef(orderRef);
        return new ApiResponse<>("OK", "Payment fetched for orderRef=" + orderRef, res);
    }

    // --- fallback (handles 404 vs other errors) ---
    public ApiResponse<PaymentRes> paymentByOrderRefFallback(String orderRef, Throwable t) {
        if (t instanceof FeignException.NotFound) {
            return new ApiResponse<>("NOT_FOUND", "No payment found for orderRef=" + orderRef, null);
        }
        return new ApiResponse<>("ERROR", "Payments service unavailable for orderRef=" + orderRef, null);
    }
}
