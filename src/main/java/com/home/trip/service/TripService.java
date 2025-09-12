package com.home.trip.service;

import com.home.trip.domain.TripSurvey;
import com.home.trip.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;

    public void save(TripSurvey tripSurvey) {
        tripRepository.save(tripSurvey);
    }
}
