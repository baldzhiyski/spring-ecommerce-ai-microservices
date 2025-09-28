package org.baldzhiyski.order.customer;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Address {

    private String street;
    private String city;
    private String houseNumber;
    private String zipCode;
}
