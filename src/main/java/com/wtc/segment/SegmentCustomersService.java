package com.wtc.segment;

import com.wtc.customer.CustomerDocument;
import com.wtc.customer.CustomerRepository;
import com.wtc.segment.dto.SegmentCustomersResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SegmentCustomersService {

    private final SegmentRepository segmentRepository;
    private final CustomerRepository customerRepository;

    public SegmentCustomersService(SegmentRepository segmentRepository,
                                   CustomerRepository customerRepository) {
        this.segmentRepository = segmentRepository;
        this.customerRepository = customerRepository;
    }

    public SegmentCustomersResponse execute(String segmentId) {

        SegmentDocument segment = segmentRepository.findById(segmentId)
                .orElseThrow(() -> new RuntimeException("Segmento não encontrado"));

        List<CustomerDocument> customers = customerRepository.findAll();

        List<CustomerDocument> filtered = customers.stream()
    .filter(c -> segment.getVip() == null || 
        (c.getVip() != null && c.getVip().equals(segment.getVip())))

    .filter(c -> segment.getActive() == null || 
        (c.getAtivo() != null && c.getAtivo().equals(segment.getActive())))

    .filter(c -> segment.getMinLoyalty() == null || 
        (c.getFidelidade() != null && c.getFidelidade())) // fidelidade = true

    // score removido porque não existe
    .toList();

        return new SegmentCustomersResponse(
                segmentId,
                filtered.size(),
                filtered.stream().map(CustomerDocument::getId).toList()
        );
    }
}