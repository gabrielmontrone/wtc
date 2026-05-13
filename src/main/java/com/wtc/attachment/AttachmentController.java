package com.wtc.attachment;

import com.wtc.attachment.dto.UploadRequest;
import com.wtc.attachment.dto.UploadResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/attachments")
@Tag(name = "Attachments", description = "Gerenciamento de mídias e uploads via S3/MinIO")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping("/upload-request")
    @Operation(summary = "Solicitar URL de Upload", description = "Gera uma URL pré-assinada válida por 15 minutos para upload direto ao storage.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "URL gerada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - Requer Token JWT")
    })
    public ResponseEntity<UploadResponse> createUploadRequest(@RequestBody @Valid UploadRequest request) {
        UploadResponse response = attachmentService.prepareUpload(request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{attachmentId}/confirm")
    @Operation(summary = "Confirmar Upload", description = "Atualiza o status do anexo para 'UPLOADED' e vincula o arquivo a uma mensagem específica do chat.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Upload confirmado e vinculado com sucesso"),
            @ApiResponse(responseCode = "404", description = "ID de anexo não encontrado")
    })
    public ResponseEntity<Void> confirmUpload(
            @Parameter(description = "ID do registro de anexo") @PathVariable String attachmentId,
            @Parameter(description = "ID da mensagem que contém este anexo") @RequestParam String messageId) {
        attachmentService.confirmUpload(attachmentId, messageId);
        return ResponseEntity.ok().build();
    }
}