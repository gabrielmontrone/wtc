package com.wtc.audit;

import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
public class AuditService {
    private final AuditLogRepository repository;

    public AuditService(AuditLogRepository repository) {
        this.repository = repository;
    }

    public void log(String action, String userEmail, String details) {
        AuditLogDocument doc = new AuditLogDocument();
        doc.setAction(action);
        doc.setUserEmail(userEmail);
        doc.setDetails(details);
        doc.setTimestamp(Instant.now().toString());
        doc.setCorrelationId(MDC.get("correlationId"));

        repository.save(doc);
        System.out.println("AUDIT: " + action + " por " + userEmail);
    }
}