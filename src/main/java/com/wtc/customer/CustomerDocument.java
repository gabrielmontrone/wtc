package com.wtc.customer;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "customers")
public class CustomerDocument {

    @Id
    private String id;

    private String name;
    private String document;

    private Boolean vip;
    private Boolean fidelidade;
    private Boolean ativo;

    private LocalDateTime createdAt;

    // getters e setters
    
}
