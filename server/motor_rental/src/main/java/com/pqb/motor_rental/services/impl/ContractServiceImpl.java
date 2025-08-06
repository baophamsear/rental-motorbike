package com.pqb.motor_rental.services.impl;

import com.pqb.motor_rental.entities.Motorbike;
import com.pqb.motor_rental.entities.RentalContract;
import com.pqb.motor_rental.entities.User;
import com.pqb.motor_rental.enums.BikeStatus;
import com.pqb.motor_rental.enums.ContractStatus;
import com.pqb.motor_rental.repositories.MotorbikeRepository;
import com.pqb.motor_rental.repositories.RentalContractRepository;
import com.pqb.motor_rental.repositories.UserRepository;
import com.pqb.motor_rental.services.ContractService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContractServiceImpl implements ContractService {

    private RentalContractRepository contractRepository;
    private MotorbikeRepository motorbikeRepository;
    private UserRepository userRepository;

    public ContractServiceImpl(RentalContractRepository contractRepository,
                               MotorbikeRepository motorbikeRepository, UserRepository userRepository) {
        this.contractRepository = contractRepository;
        this.motorbikeRepository = motorbikeRepository;
        this.userRepository = userRepository;
    }



    @Override
    public RentalContract createContract(RentalContract rentalContract) {
        Integer motorbikeId = rentalContract.getContractId().intValue();

        Motorbike bike = motorbikeRepository.findById(motorbikeId.longValue())
                .orElseThrow(() -> new RuntimeException("Motorbike not found"));

        if (bike.getStatus() != BikeStatus.available){
            throw new RuntimeException("Motorbike is not available");
        }

        boolean hasActive = contractRepository.existsByBikeAndStatus(bike, ContractStatus.active);
        if (hasActive){
            throw new RuntimeException("Contract already exists");
        }

        User lessor = userRepository.findById(rentalContract.getLessor().getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        RentalContract contract = new RentalContract();
        contract.setBike(bike);
        contract.setLessor(lessor);
        contract.setServiceFee(rentalContract.getServiceFee());
        contract.setStatus(ContractStatus.pending);
        contract.setPaymentCycle(rentalContract.getPaymentCycle());
        contract.setStartDate(rentalContract.getStartDate());
        contract.setEndDate(rentalContract.getEndDate());

        contractRepository.save(contract);
        return contract;
    }

    // Lấy danh sách các contract đang ở trạng thái pending
    @Override
    public List<RentalContract> getPendingContracts() {
        return contractRepository.findByStatus(ContractStatus.pending);
    }

    // Cập nhật lại contract
    @Override
    public RentalContract approveContract(Long contractId, User adminUser) {
        RentalContract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        if (contract.getStatus() != ContractStatus.pending){
            throw new RuntimeException("Contract is not pending");
        }

        contract.setStatus(ContractStatus.active);
        contract.setApprovedBy(adminUser);
        contract.setApprovedAt(LocalDateTime.now());

        return contractRepository.save(contract);
    }

}
