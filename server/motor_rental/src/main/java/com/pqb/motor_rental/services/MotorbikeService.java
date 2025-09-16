package com.pqb.motor_rental.services;

import com.pqb.motor_rental.dto.BikeStatusUpdateRequest;
import com.pqb.motor_rental.entities.Motorbike;
import com.pqb.motor_rental.enums.BikeStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MotorbikeService {
    Motorbike createMotorbike(Motorbike motorbike, String email);
    List<Motorbike> getAllMotorbikes();
    List<Motorbike> findByStatus(BikeStatus status);
    Motorbike approveMotorbike(Long motorbikeId);
    Motorbike findById(Long motorbikeId);
    List<Motorbike> getMotorbikesByUserId(Long userId);
    void updateStatuses(BikeStatusUpdateRequest request);
    void updateMotorAvailable(Long motorbikeId);
    List<Motorbike> getAvailableMotorbikes();
    List<Motorbike> getMotorbikesNearby(double lat, double lng, double radiusKm);
    Page<Motorbike> getMotorbikesByOwner(Integer ownerId, int page, int limit);
    Motorbike getMotorbikeForOwner(Integer bikeId, Integer ownerId);
}
