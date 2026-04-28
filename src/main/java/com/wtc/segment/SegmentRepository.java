package com.wtc.segment;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SegmentRepository extends MongoRepository<SegmentDocument, String> {
}