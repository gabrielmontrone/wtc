package com.wtc.campaign;

import com.wtc.campaign.dto.CampaignRequest;
import com.wtc.campaign.dto.CampaignResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}