package com.home.trip.repository;

import com.home.trip.domain.TripSurvey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<TripSurvey, Long> {

}
