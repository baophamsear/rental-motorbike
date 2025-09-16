package com.pqb.motor_rental.repositories;

import com.pqb.motor_rental.entities.Motorbike;
import com.pqb.motor_rental.entities.RentalContract;
import com.pqb.motor_rental.entities.User;
import com.pqb.motor_rental.enums.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalContractRepository extends JpaRepository<RentalContract, Long> {
    List<RentalContract> findByLessor(User lessor);
    List<RentalContract> findByBike(Motorbike motorbike);
    List<RentalContract> findByStatus(ContractStatus status);
    boolean existsByBikeAndStatus(Motorbike bike, ContractStatus status);

}
