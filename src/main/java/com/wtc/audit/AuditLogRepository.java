package com.wtc.audit;

public interface AuditLogRepository extends org.springframework.data.mongodb.repository.MongoRepository<AuditLogDocument, String> {}