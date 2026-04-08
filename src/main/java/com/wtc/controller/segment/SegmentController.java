package com.wtc.controller.segment;

import com.wtc.controller.segment.dto.SegmentResponse;
import com.wtc.customer.CustomerDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/segments")
public class SegmentController {

    @Autowired
    private SegmentService segmentService;

    // Criar um novo segmento
    @PostMapping
    public ResponseEntity<SegmentDocument> create(@RequestBody SegmentDocument segment) {
        return ResponseEntity.ok(segmentService.save(segment));
    }

    // Listar todos os segmentos com a contagem de clientes
    @GetMapping
    public ResponseEntity<List<SegmentResponse>> listAll() {
        return ResponseEntity.ok(segmentService.listAll());
    }

    // Consultar clientes que pertencem a um segmento específico
    @GetMapping("/{id}/customers")
    public ResponseEntity<List<CustomerDocument>> getCustomers(@PathVariable String id) {
        return ResponseEntity.ok(segmentService.getCustomersInSegment(id));
    }
}