package com.pqb.motor_rental.services;

import com.pqb.motor_rental.dto.RentalRequest;
import com.pqb.motor_rental.dto.RentalUpdateRequest;
import com.pqb.motor_rental.entities.Rental;
import com.pqb.motor_rental.enums.RentalStatus;
import com.pqb.motor_rental.security.CustomUserDetails;
import org.springframework.data.domain.Page;


import java.awt.print.Pageable;
import java.util.List;

public interface RentalService {
    Rental createRental(RentalRequest request, CustomUserDetails userDetails);
    List<Rental> getAllRentalsByUser(Long userId);
    void updateRentalAvailable(CustomUserDetails userDetails, Long rentalId, RentalUpdateRequest dto);
    long getTotalContractsByLessor(Integer lessorId);
    long getTotalMotorbikesByLessor(Integer lessorId);



    Page<Rental> getPendingRentalsByLessor(Integer lessorId, int page, int limit);
    Page<Rental> getConfirmedRentalsByLessor(Integer lessorId, int page, int limit);
    Page<Rental> getActiveRentalsByLessor(Integer lessorId, int page, int limit);
    Page<Rental> getAllRentalsByLessor(Integer lessorId, int page, int limit);
    Page<Rental> getCancelledRentalsByLessor(Integer lessorId, int page, int limit);
    Page<Rental> getCompletedRentalsByLessor(Integer lessorId, int page, int limit);

//    Rental findRentalById(Long id);

    Rental getRentalByIdAndLessor(Integer rentalId, Integer userId);
    Rental getRentalByIdAndRenter(Integer rentalId, Integer userId);
    void updateRentalStatusByLessor(Integer rentalId, Integer lessorId, RentalStatus status);
}
