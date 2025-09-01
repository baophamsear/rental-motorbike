package com.pqb.motor_rental.services.impl;

import com.pqb.motor_rental.dto.RentalRequest;
import com.pqb.motor_rental.entities.Rental;
import com.pqb.motor_rental.entities.RentalContract;
import com.pqb.motor_rental.entities.User;
import com.pqb.motor_rental.repositories.ContractRepository;
import com.pqb.motor_rental.repositories.RentalRepository;
import com.pqb.motor_rental.repositories.UserRepository;
import com.pqb.motor_rental.security.CustomUserDetails;
import com.pqb.motor_rental.services.RentalService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RentalServiceImpl implements RentalService {

    private final UserRepository userRepository;
    private final ContractRepository contractRepository;
    private final RentalRepository rentalRepository;

    public RentalServiceImpl(UserRepository userRepository, ContractRepository contractRepository, RentalRepository rentalRepository) {
        this.userRepository = userRepository;
        this.contractRepository = contractRepository;
        this.rentalRepository = rentalRepository;
    }

    @Override
    public Rental createRental(RentalRequest request, CustomUserDetails userDetails) {
        User renter = userRepository.findByEmail(userDetails.getUser().getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Lấy contract
        RentalContract contract = contractRepository.findById(request.getContractId())
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        // Tạo rental mới
        Rental rental = new Rental();
        rental.setRenter(renter);
        rental.setRentalContract(contract);
        rental.setStartDate(request.getStartDate());
        rental.setEndDate(request.getEndDate());
        rental.setTotalPrice(request.getTotalPrice());
        rental.setCreatedAt(LocalDateTime.now());

        rentalRepository.save(rental);
        return rental;
    }


}
