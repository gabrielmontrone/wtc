package com.wtc.observation;

import com.wtc.observation.dto.ObservationRequest;
import com.wtc.observation.dto.ObservationResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/customers/{customerId}/observations")
public class CustomerObservationController {

    private final CustomerObservationService service;

    public CustomerObservationController(CustomerObservationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ObservationResponse> add(
            @PathVariable String customerId,
            @RequestBody @Valid ObservationRequest request) {
        return ResponseEntity.ok(service.add(customerId, request));
    }

    @GetMapping
    public ResponseEntity<List<ObservationResponse>> get(@PathVariable String customerId) {
        return ResponseEntity.ok(service.listByCustomer(customerId));
    }
}