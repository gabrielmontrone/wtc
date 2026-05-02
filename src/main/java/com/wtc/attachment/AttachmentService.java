package com.wtc.attachment;

import com.wtc.attachment.dto.UploadRequest;
import com.wtc.attachment.dto.UploadResponse;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.UUID;

@Service
public class AttachmentService {

    private final AttachmentRepository repository;
    private final S3Service s3Service;

    public AttachmentService(AttachmentRepository repository, S3Service s3Service) {
        this.repository = repository;
        this.s3Service = s3Service;
    }

    public UploadResponse prepareUpload(UploadRequest request) {
        // 1. Validar tamanho (Critério de aceitação: limite de 10MB por ex)
        if (request.fileSize() > 10 * 1024 * 1024) {
            throw new RuntimeException("Arquivo muito grande! Limite de 10MB.");
        }

        // 2. Gerar um nome único para o arquivo no S3
        String s3Key = UUID.randomUUID() + "-" + request.fileName();

        // 3. Gerar a URL pré-assinada (Onde o usuário vai fazer o upload)
        String uploadUrl = s3Service.generatePresignedUrl(s3Key, request.contentType());

        // 4. Salvar metadados no MongoDB (Critério: persistir metadados)
        AttachmentDocument doc = new AttachmentDocument();
        doc.setFileName(request.fileName());
        doc.setContentType(request.contentType());
        doc.setFileSize(request.fileSize());
        doc.setS3Key(s3Key);
        doc.setCreatedAt(Instant.now());

        AttachmentDocument saved = repository.save(doc);

        return new UploadResponse(saved.getId(), uploadUrl);
    }

    public void confirmUpload(String attachmentId, String messageId) {
        AttachmentDocument doc = repository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Anexo não encontrado"));

        doc.setStatus("UPLOADED");
        doc.setMessageId(messageId); // Aqui associamos o arquivo à mensagem do chat
        repository.save(doc);
    }
}