package com.pqb.motor_rental.repositories;

import com.pqb.motor_rental.entities.Motorbike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MotorbikeRepository extends JpaRepository<Motorbike, Long> {
}
