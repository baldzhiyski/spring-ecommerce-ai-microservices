package org.baldzhiyski.customer.service.impl;

import org.baldzhiyski.customer.dto.req.CustomerRequest;
import org.baldzhiyski.customer.dto.req.UpdateRequest;
import org.baldzhiyski.customer.dto.res.CustomerRes;
import org.baldzhiyski.customer.dto.res.JSONResponse;
import org.baldzhiyski.customer.exception.CustomerNotFoundException;
import org.baldzhiyski.customer.mapper.CustomerMapper;
import org.baldzhiyski.customer.model.Customer;
import org.baldzhiyski.customer.repository.CustomerRepository;
import org.baldzhiyski.customer.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    public JSONResponse createCustomer(CustomerRequest customer) {
        Customer mapped = customerMapper.toEntity(customer);
        Customer saved = this.customerRepository.save(mapped);
        return JSONResponse.builder()
                .message(String.format("Successfully created customer with id %s", saved.getId()))
                .status(HttpStatus.CREATED.toString())
                .build();
    }

    @Override
    public JSONResponse updateCustomer(UpdateRequest customerReq) {

        Customer customer = customerRepository.findById(customerReq.id())
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer with id %s does not exist !",customerReq.id())));
        Customer updated = mergeCustomer(customer, customerReq);
        this.customerRepository.save(updated);

        return JSONResponse.builder()
                .message(String.format("Successfully updated customer with id %s", customer.getId()))
                .status(HttpStatus.OK.toString())
                .build();
    }

    @Override
    public List<CustomerRes> getAllCustomers() {
        return this.customerRepository
                .findAll()
                .stream()
                .map(customerMapper::toDTO)
                .toList();
    }

    @Override
    public CustomerRes getCustomerById(String id) {
        Customer customer = this.customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer with id %s does not exist !", id)));
        CustomerRes dto = customerMapper.toDTO(customer);

        return dto;
    }

    @Override
    public JSONResponse deleteById(String id) {
        this.customerRepository.deleteById(id);

        return JSONResponse
                .builder()
                .message(String.format("Successfully deleted customer with id %s", id))
                .status(HttpStatus.OK.toString())
                .build();
    }

    private Customer mergeCustomer(Customer customer, UpdateRequest customerReq) {
        if(!customerReq.firstName().isBlank())  customer.setFirstName(customerReq.firstName());
        if(!customerReq.lastName().isBlank())  customer.setLastName(customerReq.lastName());
        if(!customerReq.email().isBlank())  customer.setEmail(customerReq.email());
        if(customerReq.address() !=  null)  customer.setAddress(customerReq.address());
        return customer;
    }
}
