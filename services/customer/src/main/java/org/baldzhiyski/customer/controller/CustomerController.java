package org.baldzhiyski.customer.controller;

import jakarta.validation.Valid;
import org.baldzhiyski.customer.dto.req.CustomerRequest;
import org.baldzhiyski.customer.dto.req.UpdateRequest;
import org.baldzhiyski.customer.dto.res.CustomerRes;
import org.baldzhiyski.customer.dto.res.JSONResponse;
import org.baldzhiyski.customer.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {


    private final CustomerService customerService;
    public CustomerController(CustomerService customerService1) {
        this.customerService = customerService1;
    }

    @PostMapping
    public ResponseEntity<JSONResponse> createCustomer(@RequestBody @Valid CustomerRequest customer) {
        return ResponseEntity.ok(customerService.createCustomer(customer));
    }

    @PutMapping
    public ResponseEntity<JSONResponse> updateCustomer(@RequestBody @Valid UpdateRequest customer) {
        return  ResponseEntity.ok(customerService.updateCustomer(customer));
    }

    @GetMapping("/all")
    public ResponseEntity<List<CustomerRes>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerRes> getCustomerById(@PathVariable String id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<JSONResponse> deleteCustomerByid(@PathVariable String id) {
        return ResponseEntity.ok(customerService.deleteById(id));
    }
}
