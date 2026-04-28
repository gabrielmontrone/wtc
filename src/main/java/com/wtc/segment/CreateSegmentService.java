package com.wtc.segment;

import com.wtc.segment.dto.CreateSegmentRequest;
import com.wtc.segment.dto.SegmentResponse;
import org.springframework.stereotype.Service;

@Service
public class CreateSegmentService {

    private final SegmentRepository repository;

    public CreateSegmentService(SegmentRepository repository) {
        this.repository = repository;
    }

    public SegmentResponse execute(CreateSegmentRequest request) {

        SegmentDocument segment = new SegmentDocument(
                request.name(),
                request.vip(),
                request.active(),
                request.minScore(),
                request.minLoyalty()
        );

        SegmentDocument saved = repository.save(segment);

        return new SegmentResponse(
                saved.getId(),
                saved.getName(),
                saved.getVip(),
                saved.getActive(),
                saved.getMinScore(),
                saved.getMinLoyalty()
        );
    }
}