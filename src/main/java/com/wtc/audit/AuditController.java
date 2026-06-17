package com.wtc.audit;

import com.wtc.audit.dto.AuditResponse;
import com.wtc.audit.dto.AuditSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit")
@Tag(name = "Auditoria", description = "Trilha de auditoria de ações e eventos de compliance")
@SecurityRequirement(name = "bearerAuth")
public class AuditController {

    private final AuditLogRepository repository;
    private final AuditSummaryService summaryService;

    public AuditController(AuditLogRepository repository, AuditSummaryService summaryService) {
        this.repository = repository;
        this.summaryService = summaryService;
    }

    @GetMapping
    @Operation(summary = "Listar eventos de auditoria", description = "Retorna os 100 eventos mais recentes (mais novos primeiro).")
    public ResponseEntity<List<AuditResponse>> list() {
        List<AuditResponse> events = repository.findTop100ByOrderByTimestampDesc().stream()
                .map(doc -> new AuditResponse(
                        doc.getId(),
                        doc.getAction(),
                        doc.getUserEmail(),
                        doc.getDetails(),
                        doc.getTimestamp()))
                .toList();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/summary")
    @Operation(
            summary = "Resumo da auditoria",
            description = "Agrega os eventos (opcionalmente por janela de datas ISO-8601) para o dashboard.")
    public ResponseEntity<AuditSummaryResponse> summary(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        return ResponseEntity.ok(summaryService.summarize(from, to));
    }
}
