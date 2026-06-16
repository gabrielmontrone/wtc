package com.wtc.attachment.dto;

public record UploadResponse(
        String attachmentId,
        String uploadUrl, // Esta é a URL pré-assinada (PUT) para enviar os bytes
        String fileUrl    // URL pública para baixar/exibir o arquivo após o upload
) {}