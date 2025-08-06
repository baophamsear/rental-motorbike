package com.pqb.motor_rental.services;

import com.pqb.motor_rental.entities.RentalContract;
import com.pqb.motor_rental.entities.User;

import java.util.List;

public interface ContractService {
    RentalContract createContract(RentalContract rentalContract);
    List<RentalContract> getPendingContracts();
    RentalContract approveContract(Long contractId, User adminUser);
}
