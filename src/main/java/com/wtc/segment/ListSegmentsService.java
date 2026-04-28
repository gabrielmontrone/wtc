package com.wtc.segment;

import com.wtc.segment.dto.SegmentResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListSegmentsService {

    private final SegmentRepository repository;

    public ListSegmentsService(SegmentRepository repository) {
        this.repository = repository;
    }

    public List<SegmentResponse> execute() {
        return repository.findAll().stream()
                .map(s -> new SegmentResponse(
                        s.getId(),
                        s.getName(),
                        s.getVip(),
                        s.getActive(),
                        s.getMinScore(),
                        s.getMinLoyalty()
                ))
                .toList();
    }
}