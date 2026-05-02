package com.wtc.attachment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UploadRequest(
        @NotBlank String fileName,
        @NotBlank String contentType,
        @NotNull Long fileSize
) {}