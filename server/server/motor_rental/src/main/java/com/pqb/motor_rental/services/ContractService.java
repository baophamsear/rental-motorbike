package com.pqb.motor_rental.services;

import com.pqb.motor_rental.dto.ContractUpdateRequest;
import com.pqb.motor_rental.entities.RentalContract;
import com.pqb.motor_rental.entities.User;
import com.pqb.motor_rental.enums.ContractStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    void rejectContract(Long contractId, String rejectedReason);
    RentalContract getContractById(Long id);
    Page<RentalContract> getActiveContracts(Pageable pageable);
}
