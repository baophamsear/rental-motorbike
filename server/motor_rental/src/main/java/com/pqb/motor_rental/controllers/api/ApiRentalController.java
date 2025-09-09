package com.pqb.motor_rental.controllers.api;

import com.pqb.motor_rental.dto.RentalRequest;
import com.pqb.motor_rental.dto.RentalStatusUpdateRequest;
import com.pqb.motor_rental.dto.RentalUpdateRequest;
import com.pqb.motor_rental.entities.Motorbike;
import com.pqb.motor_rental.entities.Rental;
import com.pqb.motor_rental.entities.RentalContract;
import com.pqb.motor_rental.enums.RentalStatus;
import com.pqb.motor_rental.security.CustomUserDetails;
import com.pqb.motor_rental.services.RentalService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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

    @PatchMapping("/{rentalId}")
    @PreAuthorize("hasRole('renter')")
    public ResponseEntity<?> updateRental(
            @PathVariable Long rentalId,
            @RequestBody RentalUpdateRequest dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        rentalService.updateRentalAvailable(userDetails, rentalId, dto);
        return ResponseEntity.ok("Cập nhật thành công");
    }

    @GetMapping("/renter/pending")
    @PreAuthorize("hasRole('lessor')")
    public ResponseEntity<?> getPendingRentals(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Rental> rentals = rentalService.getPendingRentalsByLessor(userDetails.getUser().getUserId());
        return ResponseEntity.ok(rentals);
    }


    @GetMapping("/{rentalId}")
    @PreAuthorize("hasRole('lessor')") // hoặc 'renter' nếu cần
    public ResponseEntity<?> getRentalDetail(
            @PathVariable Integer rentalId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Rental rental = rentalService.getRentalByIdAndLessor(rentalId, userDetails.getUser().getUserId());
        return ResponseEntity.ok(rental);
    }


    @PatchMapping("/{rentalId}/status")
    @PreAuthorize("hasRole('lessor')")
    public ResponseEntity<?> updateRentalStatus(
            @PathVariable Integer rentalId,
            @RequestBody(required = false) RentalStatusUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        RentalStatus status = (request != null && request.getStatus() != null)
                ? request.getStatus()
                : RentalStatus.pending;

        rentalService.updateRentalStatusByLessor(rentalId, userDetails.getUser().getUserId(), status);
        return ResponseEntity.ok("Cập nhật trạng thái đơn thuê thành công.");
    }


//    @GetMapping("/my")
//    public ResponseEntity<List<Motorbike>> getMotorByUserId(@AuthenticationPrincipal CustomUserDetails userDetails){
//        Integer userIdInt = userDetails.getUser().getUserId();
//        Long myUserId = userIdInt.longValue();
//        return ResponseEntity.ok(motorbikeService.getMotorbikesByUserId(myUserId));
//    }
}
