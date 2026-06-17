package com.wtc.audit;

import java.util.List;

public interface AuditLogRepository extends org.springframework.data.mongodb.repository.MongoRepository<AuditLogDocument, String> {

    // timestamp é gravado como ISO-8601 UTC (Instant.toString()), então a ordenação
    // lexicográfica desc coincide com a ordem cronológica (mais recentes primeiro).
    List<AuditLogDocument> findTop100ByOrderByTimestampDesc();

    // Para o resumo (dashboard): janelas por data. Como o timestamp é ISO-8601 UTC, a
    // comparação lexicográfica equivale à cronológica.
    List<AuditLogDocument> findByTimestampGreaterThanEqual(String from);

    List<AuditLogDocument> findByTimestampLessThanEqual(String to);

    List<AuditLogDocument> findByTimestampBetween(String from, String to);
}