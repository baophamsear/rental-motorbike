package com.pqb.motor_rental.repositories;

import com.pqb.motor_rental.entities.RentalContract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractRepository extends JpaRepository<RentalContract, Long> {
    List<RentalContract> findByLessorUserId(Long lessorId);
}
