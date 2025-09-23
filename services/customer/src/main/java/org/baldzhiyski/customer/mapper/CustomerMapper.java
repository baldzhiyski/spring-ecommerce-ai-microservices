package org.baldzhiyski.customer.mapper;

import org.baldzhiyski.customer.dto.req.CustomerRequest;
import org.baldzhiyski.customer.dto.res.CustomerRes;
import org.baldzhiyski.customer.model.Customer;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CustomerMapper {

    Customer toEntity(CustomerRequest customerRequest);

    CustomerRes toDTO(Customer customer);
}
