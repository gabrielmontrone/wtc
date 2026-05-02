package com.wtc.observation;

import com.wtc.observation.dto.ObservationRequest;
import com.wtc.observation.dto.ObservationResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerObservationService {

    private final CustomerObservationRepository repository;

    public CustomerObservationService(CustomerObservationRepository repository) {
        this.repository = repository;
    }

    public ObservationResponse add(String customerId, ObservationRequest request) {
        // Pega o e-mail do operador logado no momento através do Token JWT
        String authorEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        CustomerObservationDocument doc = new CustomerObservationDocument();
        doc.setCustomerId(customerId);
        doc.setContent(request.content());
        doc.setAuthorEmail(authorEmail);
        doc.setCreatedAt(Instant.now());

        CustomerObservationDocument saved = repository.save(doc);
        return toResponse(saved);
    }

    public List<ObservationResponse> listByCustomer(String customerId) {
        return repository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private ObservationResponse toResponse(CustomerObservationDocument doc) {
        return new ObservationResponse(
                doc.getId(), doc.getCustomerId(), doc.getContent(),
                doc.getAuthorEmail(), doc.getCreatedAt()
        );
    }
}