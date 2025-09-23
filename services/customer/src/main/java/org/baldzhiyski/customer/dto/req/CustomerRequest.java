package org.baldzhiyski.customer.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.baldzhiyski.customer.model.Address;

public record CustomerRequest(
        @NotNull(message = "First Name is required") String firstName, @NotNull(message = "Last Name is required") String lastName,
        @Email(message = "Invalid email address") String email,  Address address
) {
}
