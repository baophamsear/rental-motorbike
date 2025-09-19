package com.pqb.motor_rental.services.impl;

import com.pqb.motor_rental.dto.RentalRequest;
import com.pqb.motor_rental.dto.RentalUpdateRequest;
import com.pqb.motor_rental.entities.Motorbike;
import com.pqb.motor_rental.entities.Rental;
import com.pqb.motor_rental.entities.RentalContract;
import com.pqb.motor_rental.entities.User;
import com.pqb.motor_rental.enums.BikeStatus;
import com.pqb.motor_rental.enums.PaymentStatus;
import com.pqb.motor_rental.enums.RentalPaymentStatus;
import com.pqb.motor_rental.enums.RentalStatus;
import com.pqb.motor_rental.repositories.ContractRepository;
import com.pqb.motor_rental.repositories.MotorbikeRepository;
import com.pqb.motor_rental.repositories.RentalRepository;
import com.pqb.motor_rental.repositories.UserRepository;
import com.pqb.motor_rental.security.CustomUserDetails;
import com.pqb.motor_rental.services.RentalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RentalServiceImpl implements RentalService {

    private final UserRepository userRepository;
    private final ContractRepository contractRepository;
    private final RentalRepository rentalRepository;
    private final MotorbikeRepository motorbikeRepository;

    public RentalServiceImpl(UserRepository userRepository, ContractRepository contractRepository,
                             RentalRepository rentalRepository, MotorbikeRepository motorbikeRepository) {
        this.userRepository = userRepository;
        this.contractRepository = contractRepository;
        this.rentalRepository = rentalRepository;
        this.motorbikeRepository = motorbikeRepository;
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
        rental.setPaymentStatus(RentalPaymentStatus.pending);

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

//        rental.setStatus(RentalStatus.active);
        Motorbike bike = rental.getRentalContract().getBike();

        bike.setStatus(BikeStatus.rented);
        rental.setStartDate(dto.getStartDate());
        rental.setEndDate(dto.getEndDate());
        rental.setUpdatedAt(LocalDateTime.now());
        rental.setTotalPrice(dto.getTotalAmount());
        rental.setPaymentStatus(RentalPaymentStatus.paid);
        rentalRepository.save(rental);
    }

    @Override
    public long getTotalContractsByLessor(Integer lessorId) {
        return contractRepository.countByLessor_UserId(lessorId);
    }

    @Override
    public long getTotalMotorbikesByLessor(Integer lessorId) {
        return motorbikeRepository.countByOwner_UserId(lessorId);
    }

//    @Override
//    public List<Rental> getPendingRentalsByLessor(Integer userId) {
//        return rentalRepository.findByStatusAndRentalContract_Lessor_UserId(RentalStatus.pending, userId);
//    }

    @Override
    public Page<Rental> getPendingRentalsByLessor(Integer lessorId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        return rentalRepository.findByStatusAndRentalContract_Lessor_UserId(RentalStatus.pending, lessorId, pageable);
    }
//    @Override
//    public List<Rental> getConfirmedRentalsByLessor(Integer userId) {
//        return rentalRepository.findByStatusAndRentalContract_Lessor_UserId(RentalStatus.confirmed, userId);
//    }

    @Override
    public Page<Rental> getConfirmedRentalsByLessor(Integer lessorId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        return rentalRepository.findByStatusAndRentalContract_Lessor_UserId(RentalStatus.confirmed, lessorId, pageable);
    }



//    @Override
//    public List<Rental> getActiveRentalsByLessor(Integer userId) {
//        return rentalRepository.findByStatusAndRentalContract_Lessor_UserId(RentalStatus.active, userId);
//    }

    @Override
    public Page<Rental> getActiveRentalsByLessor(Integer lessorId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        return rentalRepository.findByStatusAndRentalContract_Lessor_UserId(RentalStatus.active, lessorId, pageable);
    }


//    @Override
//    public List<Rental> getCompletedRentalsByLessor(Integer userId) {
//        return rentalRepository.findByStatusAndRentalContract_Lessor_UserId(RentalStatus.active, userId);
//    }

    @Override
    public Page<Rental> getCompletedRentalsByLessor(Integer lessorId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        return rentalRepository.findByStatusAndRentalContract_Lessor_UserId(RentalStatus.completed, lessorId, pageable);
    }

//    @Override
//    public List<Rental> getCancelledRentalsByLessor(Integer userId) {
//        return rentalRepository.findByStatusAndRentalContract_Lessor_UserId(RentalStatus.cancelled, userId);
//    }

    @Override
    public Page<Rental> getCancelledRentalsByLessor(Integer lessorId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        return rentalRepository.findByStatusAndRentalContract_Lessor_UserId(RentalStatus.cancelled, lessorId, pageable);
    }

//    @Override
//    public List<Rental> getAllRentalsByLessor(Integer lessorId) {
//        return rentalRepository.findByRentalContract_Lessor_UserId(lessorId);
//    }

    @Override
    public Page<Rental> getAllRentalsByLessor(Integer lessorId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        return rentalRepository.findByRentalContract_Lessor_UserId(lessorId, pageable);
    }


//    @Override
//    public Rental findRentalById(Long id) {
//        return rentalRepository.findById(id);
//    }

    @Override
    public Rental getRentalByIdAndLessor(Integer rentalId, Integer userId) {
        return rentalRepository.findByIdAndLessor(rentalId, userId)
                .orElseThrow(() -> new AccessDeniedException("Không tìm thấy hoặc bạn không có quyền xem đơn thuê này."));
    }

    @Override
    public Rental getRentalByIdAndRenter(Integer rentalId, Integer userId) {
        return rentalRepository.findByIdAndRenter(rentalId, userId)
                .orElseThrow(() -> new AccessDeniedException("Không tìm thấy hoặc bạn không có quyền xem đơn thuê này."));
    }

    @Override
    public void updateRentalStatusByLessor(Integer rentalId, Integer lessorId, RentalStatus status) {
        Rental rental = rentalRepository.findByIdAndLessor(rentalId, lessorId)
                .orElseThrow(() -> new AccessDeniedException("Bạn không có quyền cập nhật đơn thuê này"));

        rental.setStatus(status); // status sẽ là 'pending' nếu không truyền gì
        rental.setUpdatedAt(LocalDateTime.now());

        rentalRepository.save(rental);
    }



}
