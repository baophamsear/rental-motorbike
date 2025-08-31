package com.pqb.motor_rental.services.impl;

import com.pqb.motor_rental.dto.ContractUpdateRequest;
import com.pqb.motor_rental.entities.LocationPoint;
import com.pqb.motor_rental.entities.Motorbike;
import com.pqb.motor_rental.entities.RentalContract;
import com.pqb.motor_rental.entities.User;
import com.pqb.motor_rental.enums.BikeStatus;
import com.pqb.motor_rental.enums.BrandServiceFee;
import com.pqb.motor_rental.enums.ContractStatus;
import com.pqb.motor_rental.repositories.MotorbikeRepository;
import com.pqb.motor_rental.repositories.RentalContractRepository;
import com.pqb.motor_rental.repositories.UserRepository;
import com.pqb.motor_rental.services.ContractService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Override
    public List<RentalContract> createMultipleDrafts(List<Long> bikeIds) {
        List<RentalContract> contracts = new ArrayList<>();

        for (Long bikeId : bikeIds) {
            Motorbike bike = motorbikeRepository.findById(bikeId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy xe ID " + bikeId));

            if (bike.getStatus() != BikeStatus.approved) {
                throw new RuntimeException("Xe ID " + bikeId + " chưa được duyệt.");
            }


            String brandName = bike.getBrand().getName(); // ví dụ: "Honda"
            double rate = BrandServiceFee.getFeeRate(brandName);
            double serviceFee = bike.getPricePerDay() * rate;

            RentalContract c = new RentalContract();
            c.setBike(bike);
            c.setLessor(bike.getOwner());
            c.setServiceFee(serviceFee);
            c.setStatus(ContractStatus.pending);

            contracts.add(c);
        }

        return contractRepository.saveAll(contracts);
    }

    @Override
    public List<RentalContract> getContractsByUserId(Integer userId) {
        return contractRepository.findByLessor(userRepository.getUserByUserId(userId));
    }

    @Override
    public void updateContractFromLessor(Long contractId, ContractUpdateRequest dto, Integer userId) {
        RentalContract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));

//        if (!contract.getLessor().getUserId().equals(userId)) {
//            throw new AccessDeniedException("Bạn không có quyền chỉnh sửa hợp đồng này.");
//        }

        if (contract.getStatus() != ContractStatus.pending) {
            throw new IllegalStateException("Hợp đồng đã được duyệt hoặc hết hạn, không thể chỉnh sửa.");
        }

        contract.setStartDate(dto.getStartDate());
        contract.setEndDate(dto.getEndDate());
        contract.setPaymentCycle(dto.getPaymentCycle());

        contract.setStatus(ContractStatus.updated);

        contract.setUpdatedAt(LocalDateTime.now());

        LocationPoint locReq = dto.getLocationPoint();
        if (locReq != null) {
            LocationPoint newLoc = new LocationPoint();
            newLoc.setLatitude(locReq.getLatitude());
            newLoc.setLongitude(locReq.getLongitude());
            newLoc.setAddress(locReq.getAddress());
            contract.setLocation(newLoc);
        }
        contractRepository.save(contract);
    }

    @Override
    public List<RentalContract> getAllContracts() {
        return contractRepository.findAll();
    }

    @Override
    public void updateActiveContractStatus(Long contractId) {
        RentalContract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));

        if (contract.getStatus() == ContractStatus.active) {
            throw new IllegalStateException("Hợp đồng đã được duyệt!");
        }

        contract.setStatus(ContractStatus.active);
        contractRepository.save(contract);
    }

    @Override
    public List<RentalContract> getActiveContracts() {
        return contractRepository.findByStatus(ContractStatus.active);
    }

}
