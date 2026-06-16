package com.wtc.audit.dto;

/** A single audit-trail entry exposed to the operator console. */
public record AuditResponse(
        String id,
        String action,
        String userEmail,
        String details,
        String timestamp
) {}
