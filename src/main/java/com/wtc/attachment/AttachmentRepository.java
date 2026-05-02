package com.wtc.attachment;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface AttachmentRepository extends MongoRepository<AttachmentDocument, String> {
    // Caso queira buscar todos os anexos de uma mensagem específica no futuro
    List<AttachmentDocument> findByMessageId(String messageId);
}