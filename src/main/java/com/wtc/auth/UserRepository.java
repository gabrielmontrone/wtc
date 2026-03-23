package com.wtc.auth;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<UserDocument, String> {
    // -- MTD que diz se o e-mail existe no banco --
    Optional<UserDocument> findByEmail(String email);
}