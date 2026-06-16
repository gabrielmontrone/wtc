package com.wtc.campaign;

import com.wtc.campaign.dto.CampaignRequest;
import com.wtc.campaign.dto.CampaignResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Campanhas", description = "Gestão de campanhas e métricas operacionais")
@RestController
@RequestMapping("/api/v1/campaigns")
public class CampaignController {

    private final CampaignService service;

    public CampaignController(CampaignService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CampaignResponse> create(@RequestBody @Valid CampaignRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @GetMapping
    public ResponseEntity<List<CampaignResponse>> getAll() {
        return ResponseEntity.ok(service.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampaignResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PatchMapping("/{id}/simulate-send")
    @Operation(summary = "Simular envio de campanha", description = "Atualiza as métricas para fins de teste.")
    public ResponseEntity<Void> simulateSend(@PathVariable String id, @RequestParam boolean success) {
        service.updateSendMetrics(id, success);
        return ResponseEntity.ok().build();
    }
}