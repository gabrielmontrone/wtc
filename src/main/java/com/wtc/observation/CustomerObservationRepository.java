package com.wtc.observation;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CustomerObservationRepository extends MongoRepository<CustomerObservationDocument, String> {
    List<CustomerObservationDocument> findByCustomerIdOrderByCreatedAtDesc(String customerId);
}