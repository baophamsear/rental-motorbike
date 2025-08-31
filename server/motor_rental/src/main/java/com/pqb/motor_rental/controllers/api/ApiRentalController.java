package com.pqb.motor_rental.controllers.api;

import com.pqb.motor_rental.dto.RentalRequest;
import com.pqb.motor_rental.entities.RentalContract;
import com.pqb.motor_rental.security.CustomUserDetails;
import com.pqb.motor_rental.services.RentalService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
public class ApiRentalController {

    private final RentalService rentalService;
    public ApiRentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping
    @PreAuthorize("hasRole('renter')")
    public ResponseEntity<?> createRental(@RequestBody RentalRequest request,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        rentalService.createRental(request, userDetails);
        return ResponseEntity.ok("Rental created successfully");
    }
}
