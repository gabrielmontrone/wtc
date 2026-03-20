package com.wtc.customer.dto;
import jakarta.validation.constraints.NotBlank;

public class CreateCustomerRequest {
    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @NotBlank(message = "Documento é obrigatório")
    private String document;

    private Boolean vip;
    private Boolean fidelidade;
    private Boolean ativo;

    // getters e setters
}
