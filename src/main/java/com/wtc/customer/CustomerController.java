package com.wtc.customer;

import com.wtc.customer.dto.CreateCustomerRequest;
import com.wtc.customer.dto.CustomerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService service;

    // CREATE
    @PostMapping
    public ResponseEntity<CustomerResponse> create(
            @Valid @RequestBody CreateCustomerRequest request) {

        return ResponseEntity.ok(service.create(request));
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> findById(@PathVariable String id) {

        return ResponseEntity.ok(service.findById(id));
    }

    // LIST COM FILTROS
    @GetMapping
    public ResponseEntity<Page<CustomerResponse>> list(
            @RequestParam(required = false) Boolean vip,
            @RequestParam(required = false) Boolean fidelidade,
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                service.list(vip, fidelidade, ativo, pageable)
        );
    }
}