package com.home.trip.repository;

import com.home.trip.domain.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepository extends JpaRepository<Survey, Long> {

}
