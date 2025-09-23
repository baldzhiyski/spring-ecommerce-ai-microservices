package org.baldzhiyski.customer.model;


import lombok.*;
import org.springframework.validation.annotation.Validated;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Validated
public class Address {

    private String street;
    private String city;
    private String houseNumber;
    private String zipCode;
}
