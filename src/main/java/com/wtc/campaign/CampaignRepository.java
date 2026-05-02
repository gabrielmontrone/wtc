package com.wtc.campaign;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface CampaignRepository extends MongoRepository<CampaignDocument, String> {
    Optional<CampaignDocument> findByCallCode(String callCode);
}