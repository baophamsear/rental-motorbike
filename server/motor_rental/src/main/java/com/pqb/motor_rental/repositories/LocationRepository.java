package com.pqb.motor_rental.repositories;

import com.pqb.motor_rental.entities.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findAllByOrderByNameDesc();
}
