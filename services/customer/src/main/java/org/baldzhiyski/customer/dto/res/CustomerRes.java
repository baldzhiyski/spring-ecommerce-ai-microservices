package org.baldzhiyski.customer.dto.res;

import org.baldzhiyski.customer.model.Address;

public record CustomerRes(String id, String firstName, String lastName, String email, Address address) {
}
