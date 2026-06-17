package com.wtc.attachment;

import com.wtc.attachment.dto.UploadResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/attachments")
@Tag(name = "Anexos", description = "Upload e download de mídias das conversas (armazenadas no backend)")
public class AttachmentController {

    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Enviar anexo", description = "Recebe um arquivo (multipart), armazena no backend e devolve a URL para exibição.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Anexo armazenado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - Requer Token JWT")
    })
    public ResponseEntity<UploadResponse> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String contentType = file.getContentType() != null ? file.getContentType() : DEFAULT_CONTENT_TYPE;
        String id = attachmentService.store(file.getOriginalFilename(), contentType, file.getBytes());
        return ResponseEntity.ok(new UploadResponse(id, "/api/v1/attachments/" + id));
    }

    @GetMapping("/{attachmentId}")
    @Operation(summary = "Baixar anexo", description = "Devolve os bytes do anexo com o respectivo content-type.")
    public ResponseEntity<byte[]> download(@PathVariable String attachmentId) {
        AttachmentDocument doc = attachmentService.get(attachmentId);
        String contentType = doc.getContentType() != null ? doc.getContentType() : DEFAULT_CONTENT_TYPE;
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(doc.getContent());
    }
}
