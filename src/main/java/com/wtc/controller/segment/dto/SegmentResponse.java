package com.wtc.controller.segment.dto;

public class SegmentResponse {
    private String id;
    private String name;
    private Long customerCount; // Quantidade de clientes que se encaixam aqui

    // Construtor, Getters e Setters
    public SegmentResponse(String id, String name, Long customerCount) {
        this.id = id;
        this.name = name;
        this.customerCount = customerCount;
    }

    // ... getters e setters
}
