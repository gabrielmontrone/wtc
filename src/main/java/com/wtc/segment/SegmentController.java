package com.wtc.segment;

import com.wtc.segment.dto.CreateSegmentRequest;
import com.wtc.segment.dto.SegmentCustomersResponse;
import com.wtc.segment.dto.SegmentResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/segments")
public class SegmentController {

    private final CreateSegmentService createService;
    private final ListSegmentsService listService;
    private final SegmentCustomersService customersService;

    public SegmentController(CreateSegmentService createService,
                             ListSegmentsService listService,
                             SegmentCustomersService customersService) {
        this.createService = createService;
        this.listService = listService;
        this.customersService = customersService;
    }

    @PostMapping
    public ResponseEntity<SegmentResponse> create(@Valid @RequestBody CreateSegmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createService.execute(request));
    }

    @GetMapping
    public ResponseEntity<List<SegmentResponse>> list() {
        return ResponseEntity.ok(listService.execute());
    }

    @GetMapping("/{id}/customers")
    public ResponseEntity<SegmentCustomersResponse> customers(@PathVariable String id) {
        return ResponseEntity.ok(customersService.execute(id));
    }
}