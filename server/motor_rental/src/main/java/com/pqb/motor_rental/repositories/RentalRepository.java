package com.pqb.motor_rental.repositories;

import com.pqb.motor_rental.entities.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface RentalRepository extends JpaRepository<Rental, Long> {
}
