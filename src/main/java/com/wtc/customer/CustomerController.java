package com.wtc.customer;

import com.wtc.customer.dto.CreateCustomerRequest;
import com.wtc.customer.dto.CustomerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Tag(name = "Clientes", description = "Cadastro e consulta de clientes")
@RestController
@RequestMapping("/customers")
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {

    @Autowired
    private CustomerService service;

    // CREATE
    @PostMapping
    @Operation(summary = "Criar cliente", description = "Cadastra um cliente com documento e flags de classificação.")
    public ResponseEntity<CustomerResponse> create(
            @Valid @RequestBody CreateCustomerRequest request) {

        return ResponseEntity.ok(service.create(request));
    }

    // GET BY ID
    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID", description = "Retorna um único cliente pelo seu identificador.")
    public ResponseEntity<CustomerResponse> findById(@Parameter(description = "ID do cliente") @PathVariable String id) {

        return ResponseEntity.ok(service.findById(id));
    }

    // LIST COM FILTROS
    @GetMapping
    @Operation(summary = "Listar clientes", description = "Retorna clientes paginados, opcionalmente filtrados por flags de status.")
    public ResponseEntity<Page<CustomerResponse>> list(
            @Parameter(description = "Filtrar clientes VIP") @RequestParam(required = false) Boolean vip,
            @Parameter(description = "Filtrar clientes do programa de fidelidade") @RequestParam(required = false) Boolean fidelidade,
            @Parameter(description = "Filtrar clientes ativos") @RequestParam(required = false) Boolean ativo,
            @Parameter(description = "Número da página, começando em 0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                service.list(vip, fidelidade, ativo, pageable)
        );
    }
}
