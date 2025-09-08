package com.pqb.motor_rental.controllers.api;

import com.pqb.motor_rental.dto.RentalRequest;
import com.pqb.motor_rental.entities.Motorbike;
import com.pqb.motor_rental.entities.Rental;
import com.pqb.motor_rental.entities.RentalContract;
import com.pqb.motor_rental.security.CustomUserDetails;
import com.pqb.motor_rental.services.RentalService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
        Rental rental = rentalService.createRental(request, userDetails);
        return ResponseEntity.ok(Map.of("rentalId", rental.getRentalId()));
    }

    @GetMapping("/my")
    public ResponseEntity<List<Rental>> getRentalsByUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userIdInt = userDetails.getUser().getUserId();
        Long myUserId = userIdInt.longValue();
        List<Rental> rentals = rentalService.getAllRentalsByUser(myUserId);
        return ResponseEntity.ok(rentals);
    }

//    @GetMapping("/my")
//    public ResponseEntity<List<Motorbike>> getMotorByUserId(@AuthenticationPrincipal CustomUserDetails userDetails){
//        Integer userIdInt = userDetails.getUser().getUserId();
//        Long myUserId = userIdInt.longValue();
//        return ResponseEntity.ok(motorbikeService.getMotorbikesByUserId(myUserId));
//    }
}
