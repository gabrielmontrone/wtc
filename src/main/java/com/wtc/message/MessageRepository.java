package com.wtc.message;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<MessageDocument, String> {

    List<MessageDocument> findByConversationIdOrderByCreatedAtAsc(String conversationId);
}