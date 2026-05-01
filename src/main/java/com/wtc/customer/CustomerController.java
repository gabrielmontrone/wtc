package com.wtc.customer;

import com.wtc.customer.dto.CreateCustomerRequest;
import com.wtc.customer.dto.CustomerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/customers")
@Tag(name = "Customers", description = "Customer registration and queries")
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {

    @Autowired
    private CustomerService service;

    // CREATE
    @PostMapping
    @Operation(summary = "Create customer", description = "Registers a customer with document and classification flags.")
    public ResponseEntity<CustomerResponse> create(
            @Valid @RequestBody CreateCustomerRequest request) {

        return ResponseEntity.ok(service.create(request));
    }

    // GET BY ID
    @GetMapping("/{id}")
    @Operation(summary = "Find customer by ID", description = "Returns a single customer by its identifier.")
    public ResponseEntity<CustomerResponse> findById(@Parameter(description = "Customer ID") @PathVariable String id) {

        return ResponseEntity.ok(service.findById(id));
    }

    // LIST COM FILTROS
    @GetMapping
    @Operation(summary = "List customers", description = "Returns paginated customers, optionally filtered by status flags.")
    public ResponseEntity<Page<CustomerResponse>> list(
            @Parameter(description = "Filter VIP customers") @RequestParam(required = false) Boolean vip,
            @Parameter(description = "Filter loyalty program customers") @RequestParam(required = false) Boolean fidelidade,
            @Parameter(description = "Filter active customers") @RequestParam(required = false) Boolean ativo,
            @Parameter(description = "Page number, starting at 0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                service.list(vip, fidelidade, ativo, pageable)
        );
    }
}
