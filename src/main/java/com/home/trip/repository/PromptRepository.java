package com.home.trip.repository;

import com.home.trip.domain.Prompt;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface PromptRepository extends MongoRepository<Prompt, String> {
    @Query("{ 'role': 'system', 'isActive': 1 }")
    Optional<Prompt> findActiveByRole(String role);
}
