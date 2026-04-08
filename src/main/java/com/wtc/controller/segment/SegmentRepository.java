package com.wtc.controller.segment;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SegmentRepository extends MongoRepository<SegmentDocument, String> {
}
