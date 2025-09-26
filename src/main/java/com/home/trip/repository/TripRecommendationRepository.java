package com.home.trip.repository;

import com.home.trip.domain.TripRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRecommendationRepository extends JpaRepository<TripRecommendation, Long> {

}
