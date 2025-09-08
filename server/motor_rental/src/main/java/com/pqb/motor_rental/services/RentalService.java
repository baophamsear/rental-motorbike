package com.pqb.motor_rental.services;

import com.pqb.motor_rental.dto.RentalRequest;
import com.pqb.motor_rental.dto.RentalUpdateRequest;
import com.pqb.motor_rental.entities.Rental;
import com.pqb.motor_rental.security.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface RentalService {
    Rental createRental(RentalRequest request, CustomUserDetails userDetails);
    List<Rental> getAllRentalsByUser(Long userId);
    void updateRentalAvailable(CustomUserDetails userDetails, Long rentalId, RentalUpdateRequest dto);
}
