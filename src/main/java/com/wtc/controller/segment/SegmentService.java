package com.wtc.controller.segment;

import com.wtc.customer.CustomerDocument;
import com.wtc.customer.CustomerRepository;
import com.wtc.controller.segment.dto.SegmentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SegmentService {

    @Autowired
    private SegmentRepository segmentRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MongoTemplate mongoTemplate; // Usado para fazer buscas dinâmicas

    // Criar um novo segmento
    public SegmentDocument save(SegmentDocument segment) {
        return segmentRepository.save(segment);
    }

    // Listar todos os segmentos com a contagem de clientes
    public List<SegmentResponse> listAll() {
        return segmentRepository.findAll().stream().map(segment -> {
            long count = countCustomersByRules(segment);
            return new SegmentResponse(segment.getId(), segment.getName(), count);
        }).collect(Collectors.toList());
    }

    // Lógica para contar clientes baseada nas regras do segmento
    public long countCustomersByRules(SegmentDocument segment) {
        Query query = new Query();

        if (segment.getVip() != null) {
            query.addCriteria(Criteria.where("vip").is(segment.getVip()));
        }
        if (segment.getFidelidade() != null) {
            query.addCriteria(Criteria.where("fidelidade").is(segment.getFidelidade()));
        }
        if (segment.getAtivo() != null) {
            query.addCriteria(Criteria.where("ativo").is(segment.getAtivo()));
        }
        if (segment.getMinScore() != null) {
            query.addCriteria(Criteria.where("score").gte(segment.getMinScore()));
        }

        return mongoTemplate.count(query, CustomerDocument.class);
    }

    // Consultar os clientes reais que pertencem a um segmento
    public List<CustomerDocument> getCustomersInSegment(String segmentId) {
        SegmentDocument segment = segmentRepository.findById(segmentId)
                .orElseThrow(() -> new RuntimeException("Segmento não encontrado"));

        Query query = new Query();
        // Repete as mesmas regras acima para buscar a lista
        if (segment.getVip() != null) query.addCriteria(Criteria.where("vip").is(segment.getVip()));
        if (segment.getAtivo() != null) query.addCriteria(Criteria.where("ativo").is(segment.getAtivo()));
        // ... (pode ser refatorado para evitar repetição)

        return mongoTemplate.find(query, CustomerDocument.class);
    }
}