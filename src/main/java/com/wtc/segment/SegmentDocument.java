package com.wtc.segment;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "segments")
public class SegmentDocument {

    @Id
    private String id;

    private String name;
    private Boolean vip;
    private Boolean active;
    private Integer minScore;
    private Integer minLoyalty;

    public SegmentDocument() {}

    public SegmentDocument(String name, Boolean vip, Boolean active, Integer minScore, Integer minLoyalty) {
        this.name = name;
        this.vip = vip;
        this.active = active;
        this.minScore = minScore;
        this.minLoyalty = minLoyalty;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Boolean getVip() { return vip; }
    public Boolean getActive() { return active; }
    public Integer getMinScore() { return minScore; }
    public Integer getMinLoyalty() { return minLoyalty; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setVip(Boolean vip) { this.vip = vip; }
    public void setActive(Boolean active) { this.active = active; }
    public void setMinScore(Integer minScore) { this.minScore = minScore; }
    public void setMinLoyalty(Integer minLoyalty) { this.minLoyalty = minLoyalty; }
}