package com.wtc.segment;

import com.wtc.segment.dto.CreateSegmentRequest;
import com.wtc.segment.dto.SegmentCustomersResponse;
import com.wtc.segment.dto.SegmentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/segments")
@Tag(name = "Segmentos", description = "Regras de segmentação de clientes")
@SecurityRequirement(name = "bearerAuth")
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
    @Operation(summary = "Criar segmento", description = "Cria um segmento a partir de critérios de classificação de clientes.")
    public ResponseEntity<SegmentResponse> create(@Valid @RequestBody CreateSegmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createService.execute(request));
    }

    @GetMapping
    @Operation(summary = "Listar segmentos", description = "Retorna todos os segmentos configurados.")
    public ResponseEntity<List<SegmentResponse>> list() {
        return ResponseEntity.ok(listService.execute());
    }

    @GetMapping("/{id}/customers")
    @Operation(summary = "Listar clientes do segmento", description = "Retorna os IDs dos clientes que correspondem a um segmento.")
    public ResponseEntity<SegmentCustomersResponse> customers(@Parameter(description = "ID do segmento") @PathVariable String id) {
        return ResponseEntity.ok(customersService.execute(id));
    }
}
