package com.home.trip.repository;

import com.home.trip.domain.Prompt;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PromptRepository extends MongoRepository<Prompt, String> {
    List<Prompt> findByRole(String role);
}
