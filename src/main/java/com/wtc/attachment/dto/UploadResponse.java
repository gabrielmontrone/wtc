package com.wtc.attachment.dto;

public record UploadResponse(
        String attachmentId,
        String uploadUrl // Esta é a URL pré-assinada
) {}