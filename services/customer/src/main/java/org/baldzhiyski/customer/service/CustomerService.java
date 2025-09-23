package org.baldzhiyski.customer.service;

import jakarta.validation.Valid;
import org.baldzhiyski.customer.dto.req.CustomerRequest;
import org.baldzhiyski.customer.dto.req.UpdateRequest;
import org.baldzhiyski.customer.dto.res.CustomerRes;
import org.baldzhiyski.customer.dto.res.JSONResponse;

import java.util.List;

public interface CustomerService {
    JSONResponse createCustomer(@Valid CustomerRequest customer);

    JSONResponse updateCustomer(@Valid UpdateRequest customer);

    List<CustomerRes> getAllCustomers();

    CustomerRes getCustomerById(String id);

    JSONResponse deleteById(String id);
}
