package com.pqb.motor_rental.services.impl;

import com.pqb.motor_rental.dto.RentalRequest;
import com.pqb.motor_rental.dto.RentalUpdateRequest;
import com.pqb.motor_rental.entities.Rental;
import com.pqb.motor_rental.entities.RentalContract;
import com.pqb.motor_rental.entities.User;
import com.pqb.motor_rental.enums.RentalStatus;
import com.pqb.motor_rental.repositories.ContractRepository;
import com.pqb.motor_rental.repositories.RentalRepository;
import com.pqb.motor_rental.repositories.UserRepository;
import com.pqb.motor_rental.security.CustomUserDetails;
import com.pqb.motor_rental.services.RentalService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
        rental.setStatus(RentalStatus.pending);
        rental.setCreatedAt(LocalDateTime.now());

        rentalRepository.save(rental);
        return rental;
    }

    @Override
    public List<Rental> getAllRentalsByUser(Long userId) {
        return rentalRepository.findByRenter_UserId(userId);
    }

    @Override
    public void updateRentalAvailable(CustomUserDetails userDetails, Long rentalId, RentalUpdateRequest dto) {

        User currentUser = userRepository.findByEmail(userDetails.getUser().getEmail())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy rental"));

        if (!rental.getRenter().getUserId().equals(currentUser.getUserId())) {
            throw new AccessDeniedException("Bạn không có quyền cập nhật đơn thuê này");
        }

        rental.setStatus(RentalStatus.active);
        rental.setStartDate(dto.getStartDate());
        rental.setEndDate(dto.getEndDate());
        rental.setUpdatedAt(LocalDateTime.now());
        rental.setTotalPrice(dto.getTotalAmount());
        rentalRepository.save(rental);
    }

    @Override
    public List<Rental> getPendingRentalsByLessor(Integer userId) {
        return rentalRepository.findByStatusAndContract_Lessor_UserId(RentalStatus.pending, userId);
    }


}
