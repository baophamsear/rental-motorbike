package com.pqb.motor_rental.repositories;

import com.pqb.motor_rental.entities.Rental;
import com.pqb.motor_rental.enums.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findByRenter_UserId(Long userId);
    List<Rental> findByStatusAndContract_Lessor_UserId(RentalStatus status, Integer lessorId);

}
