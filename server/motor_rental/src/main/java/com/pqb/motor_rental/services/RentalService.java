package com.pqb.motor_rental.services;

import com.pqb.motor_rental.dto.RentalRequest;
import com.pqb.motor_rental.entities.Rental;
import com.pqb.motor_rental.security.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;

public interface RentalService {
    Rental createRental(RentalRequest request, CustomUserDetails userDetails);
}
