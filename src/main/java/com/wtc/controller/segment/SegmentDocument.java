package com.wtc.controller.segment;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "segments")
public class SegmentDocument {

    @Id
    private String id;
    private String name;
    private String description;

    // Critérios que o operador vai definir
    private Boolean vip;
    private Boolean fidelidade;
    private Boolean ativo;
    private Integer minScore; // Filtrar clientes com score maior ou igual a este

    // Getters e Setters (ou use @Data se tiver o Lombok no projeto)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Boolean getVip() { return vip; }
    public void setVip(Boolean vip) { this.vip = vip; }
    public Boolean getFidelidade() { return fidelidade; }
    public void setFidelidade(Boolean fidelidade) { this.fidelidade = fidelidade; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    public Integer getMinScore() { return minScore; }
    public void setMinScore(Integer minScore) { this.minScore = minScore; }
}