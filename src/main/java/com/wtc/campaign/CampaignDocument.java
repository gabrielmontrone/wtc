package com.wtc.campaign;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "campaigns")
public class CampaignDocument {
    @Id
    private String id;
    private String name;
    private String description;
    private String type;          // Ex: EMAIL, SMS, PUSH
    private String content;       // Mensagem-base
    private String segmentTargetId; // O ID do segmento do Álvaro
    private String callCode;      // Ex: "blackfriday", "promovip"
    private String status;        // DRAFT, ACTIVE, FINISHED
    private Instant createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSegmentTargetId() {
        return segmentTargetId;
    }

    public void setSegmentTargetId(String segmentTargetId) {
        this.segmentTargetId = segmentTargetId;
    }

    public String getCallCode() {
        return callCode;
    }

    public void setCallCode(String callCode) {
        this.callCode = callCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}