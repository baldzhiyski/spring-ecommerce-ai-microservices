package org.baldzhiyski.order.model.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.baldzhiyski.order.model.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

public record OrderReq(Integer id,
                       String reference,
                       @NotNull(message = "Payment method is required") PaymentMethod paymentMethod,
                       @NotBlank(message = "Customer should be present") String customerId,
                       @NotEmpty(message = "At least one product purchase is required") List<PurchaseRequest> products) {
}
