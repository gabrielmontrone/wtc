package com.wtc.message;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<MessageDocument, String> {

    // Esta linha permite que você busque o histórico de mensagens de um cliente
    List<MessageDocument> findByCustomerId(String customerId);

}