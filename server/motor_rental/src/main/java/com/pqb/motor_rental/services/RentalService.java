package com.pqb.motor_rental.services;

import com.pqb.motor_rental.dto.RentalRequest;
import com.pqb.motor_rental.dto.RentalUpdateRequest;
import com.pqb.motor_rental.entities.Rental;
import com.pqb.motor_rental.enums.RentalStatus;
import com.pqb.motor_rental.security.CustomUserDetails;


import java.util.List;

public interface RentalService {
    Rental createRental(RentalRequest request, CustomUserDetails userDetails);
    List<Rental> getAllRentalsByUser(Long userId);
    void updateRentalAvailable(CustomUserDetails userDetails, Long rentalId, RentalUpdateRequest dto);
    List<Rental> getPendingRentalsByLessor(Integer lessorId);
    Rental getRentalByIdAndLessor(Integer rentalId, Integer userId);
    void updateRentalStatusByLessor(Integer rentalId, Integer lessorId, RentalStatus status);
}
