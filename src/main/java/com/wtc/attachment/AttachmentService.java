package com.wtc.attachment;

import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
public class AttachmentService {

    private static final long MAX_FILE_SIZE_BYTES = 10L * 1024 * 1024; // 10MB

    private final AttachmentRepository repository;

    public AttachmentService(AttachmentRepository repository) {
        this.repository = repository;
    }

    /** Stores the uploaded bytes in MongoDB and returns the generated attachment id. */
    public String store(String fileName, String contentType, byte[] content) {
        if (content == null || content.length == 0) {
            throw new IllegalArgumentException("Arquivo vazio.");
        }
        if (content.length > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("Arquivo muito grande! Limite de 10MB.");
        }

        AttachmentDocument doc = new AttachmentDocument();
        doc.setFileName(fileName);
        doc.setContentType(contentType);
        doc.setFileSize((long) content.length);
        doc.setContent(content);
        doc.setCreatedAt(Instant.now());

        return repository.save(doc).getId();
    }

    public AttachmentDocument get(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Anexo não encontrado"));
    }
}
