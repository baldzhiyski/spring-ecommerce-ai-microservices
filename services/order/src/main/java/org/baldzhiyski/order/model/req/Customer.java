package org.baldzhiyski.order.model.req;

public record Customer(
        String id,
        String email,
        String firstName,
        String lastName
) {}