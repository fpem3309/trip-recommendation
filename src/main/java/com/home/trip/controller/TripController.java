package com.home.trip.controller;

import com.home.trip.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TripController {
    private final TripService tripService;
}
