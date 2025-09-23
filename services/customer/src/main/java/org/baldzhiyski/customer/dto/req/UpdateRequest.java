package org.baldzhiyski.customer.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.baldzhiyski.customer.model.Address;

public record UpdateRequest(
        @NotNull(message = "Id is required") String id,
       String firstName,  String lastName,
        String email, Address address
) {
}
