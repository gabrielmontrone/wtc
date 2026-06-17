package com.wtc.attachment.dto;

/**
 * Result of storing an attachment in the backend.
 *
 * @param id  generated attachment id
 * @param url relative path to download/display it ("/api/v1/attachments/{id}")
 */
public record UploadResponse(
        String id,
        String url
) {}
