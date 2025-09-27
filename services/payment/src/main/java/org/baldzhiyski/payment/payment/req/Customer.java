package org.baldzhiyski.payment.payment.req;

import jakarta.validation.constraints.NotNull;

public record Customer(String id,
                          @NotNull(message = "First name should be provided") String firstName,
                          @NotNull(message = "Last name should be provided") String lastName,
                          @NotNull(message = "Email should be provided")  String email) {
}
