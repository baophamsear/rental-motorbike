package com.pqb.motor_rental.repositories;

import com.pqb.motor_rental.entities.Motorbike;
import com.pqb.motor_rental.enums.BikeStatus;
import com.pqb.motor_rental.enums.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MotorbikeRepository extends JpaRepository<Motorbike, Long> {
    List<Motorbike> findByStatus(BikeStatus status);
    List<Motorbike> findByOwner_UserId(Long userId);
}
