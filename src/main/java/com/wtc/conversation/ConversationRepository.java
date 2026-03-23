package com.wtc.conversation;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ConversationRepository extends MongoRepository<ConversationDocument, String> {
    // Busca as conversas de um cliente específico (Critério de aceitação #4)
    List<ConversationDocument> findByCustomerIdOrderByUpdatedAtDesc(String customerId);
}