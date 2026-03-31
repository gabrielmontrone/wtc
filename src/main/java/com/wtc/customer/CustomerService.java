package com.wtc.customer;
import com.wtc.customer.dto.CreateCustomerRequest;
import com.wtc.customer.dto.CustomerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public CustomerResponse create(CreateCustomerRequest request) {

        CustomerDocument customer = new CustomerDocument();
        customer.setName(request.getName());
        customer.setDocument(request.getDocument());

        customer.setVip(Boolean.TRUE.equals(request.getVip()));
        customer.setFidelidade(Boolean.TRUE.equals(request.getFidelidade()));
        customer.setAtivo(Boolean.TRUE.equals(request.getAtivo()));

        customer.setCreatedAt(LocalDateTime.now());

        repository.save(customer);

        return toResponse(customer);
    }

    public CustomerResponse findById(String id) {

        CustomerDocument customer = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        return toResponse(customer);
    }

    public Page<CustomerResponse> list(Boolean vip,
                                       Boolean fidelidade,
                                       Boolean ativo,
                                       Pageable pageable) {

        Query query = new Query().with(pageable);

        if (vip != null) {
            query.addCriteria(Criteria.where("vip").is(vip));
        }

        if (fidelidade != null) {
            query.addCriteria(Criteria.where("fidelidade").is(fidelidade));
        }

        if (ativo != null) {
            query.addCriteria(Criteria.where("ativo").is(ativo));
        }

        List<CustomerDocument> customers =
                mongoTemplate.find(query, CustomerDocument.class);

        long total =
                mongoTemplate.count(query.skip(0).limit(0), CustomerDocument.class);

        List<CustomerResponse> response = customers.stream()
                .map(this::toResponse)
                .toList();

        return new PageImpl<>(response, pageable, total);
    }

    private CustomerResponse toResponse(CustomerDocument customer) {

        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setName(customer.getName());
        response.setDocument(customer.getDocument());
        response.setVip(customer.getVip());
        response.setFidelidade(customer.getFidelidade());
        response.setAtivo(customer.getAtivo());
        response.setCreatedAt(customer.getCreatedAt());

        return response;
    }
}