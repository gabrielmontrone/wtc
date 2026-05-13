package com.wtc.campaign;

import com.wtc.campaign.dto.CampaignRequest;
import com.wtc.campaign.dto.CampaignResponse;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CampaignService {

    private final CampaignRepository repository;

    public CampaignService(CampaignRepository repository) {
        this.repository = repository;
    }

    public CampaignResponse create(CampaignRequest request) {
        CampaignDocument doc = new CampaignDocument();
        doc.setName(request.name());
        doc.setDescription(request.description());
        doc.setType(request.type());
        doc.setContent(request.content());
        doc.setSegmentTargetId(request.segmentTargetId());
        doc.setCallCode(request.callCode().toLowerCase().replace(" ", "-")); // limpa o código
        doc.setStatus("DRAFT"); // Status inicial obrigatório
        doc.setCreatedAt(Instant.now());

        CampaignDocument saved = repository.save(doc);
        return toResponse(saved);
    }

    public List<CampaignResponse> listAll() {
        return repository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public CampaignResponse getById(String id) {
        return repository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Campanha não encontrada"));
    }

    // Metodo para atualizar métricas de envio (Sucesso ou Falha)
    public void updateSendMetrics(String campaignId, boolean isSuccess) {
        repository.findById(campaignId).ifPresent(doc -> {
            doc.setTotalSends(doc.getTotalSends() + 1);
            if (isSuccess) {
                doc.setSuccessSends(doc.getSuccessSends() + 1);
            } else {
                doc.setFailureSends(doc.getFailureSends() + 1);
            }
            repository.save(doc);
        });
    }

    // Metodo para incrementar quando o cliente responde
    public void incrementResponseCount(String campaignId) {
        repository.findById(campaignId).ifPresent(doc -> {
            doc.setResponseCount(doc.getResponseCount() + 1);
            repository.save(doc);
        });
    }

    private CampaignResponse toResponse(CampaignDocument doc) {
        return new CampaignResponse(
                doc.getId(), doc.getName(), doc.getDescription(), doc.getType(),
                doc.getContent(), doc.getSegmentTargetId(), doc.getCallCode(),
                doc.getStatus(), doc.getCreatedAt(), doc.getTotalSends(), doc.getSuccessSends(), doc.getFailureSends(), doc.getResponseCount()
        );
    }
}