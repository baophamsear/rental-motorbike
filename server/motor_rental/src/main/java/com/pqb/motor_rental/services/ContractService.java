package com.pqb.motor_rental.services;

import com.pqb.motor_rental.dto.ContractUpdateRequest;
import com.pqb.motor_rental.entities.RentalContract;
import com.pqb.motor_rental.entities.User;
import com.pqb.motor_rental.enums.ContractStatus;

import java.util.List;

public interface ContractService {
    RentalContract createContract(RentalContract rentalContract);
    List<RentalContract> getPendingContracts();
    RentalContract approveContract(Long contractId, User adminUser);
    List<RentalContract> createMultipleDrafts(List<Long> bikesId);
    List<RentalContract> getContractsByUserId(Integer userId);
    void updateContractFromLessor(Long contractId, ContractUpdateRequest dto, Integer userId);
    List<RentalContract> getAllContracts();
    void updateActiveContractStatus(Long contractId);
    List<RentalContract> getActiveContracts();
    List<RentalContract> getContractsNearby(double lat, double lng, double radiusKm);
}
