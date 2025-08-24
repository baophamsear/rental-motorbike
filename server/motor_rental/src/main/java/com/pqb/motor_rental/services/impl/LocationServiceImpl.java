package com.pqb.motor_rental.services.impl;

import com.pqb.motor_rental.entities.Location;
import com.pqb.motor_rental.repositories.LocationRepository;
import com.pqb.motor_rental.services.LocationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public List<Location> getAllLocations() {
        return locationRepository.findAllByOrderByNameDesc();
    }
}
