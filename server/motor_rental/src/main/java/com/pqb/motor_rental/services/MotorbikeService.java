package com.pqb.motor_rental.services;

import com.pqb.motor_rental.entities.Motorbike;
import com.pqb.motor_rental.enums.BikeStatus;

import java.util.List;

public interface MotorbikeService {
    void createMotorbike(Motorbike motorbike, String email);
    List<Motorbike> getAllMotorbikes();
    List<Motorbike> findByStatus(BikeStatus status);
    Motorbike approveMotorbike(Long motorbikeId);
    Motorbike findById(Long motorbikeId);
    List<Motorbike> getMotorbikesByUserId(Long userId);
}
