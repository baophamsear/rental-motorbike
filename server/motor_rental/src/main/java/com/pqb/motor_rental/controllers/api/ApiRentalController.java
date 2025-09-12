package com.pqb.motor_rental.controllers.api;

import com.pqb.motor_rental.dto.*;
import com.pqb.motor_rental.entities.Motorbike;
import com.pqb.motor_rental.entities.Rental;
import com.pqb.motor_rental.entities.RentalContract;
import com.pqb.motor_rental.enums.RentalPaymentStatus;
import com.pqb.motor_rental.enums.RentalStatus;
import com.pqb.motor_rental.repositories.RentalRepository;
import com.pqb.motor_rental.security.CustomUserDetails;
import com.pqb.motor_rental.services.MotorbikeService;
import com.pqb.motor_rental.services.RentalService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.time.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rentals")
public class ApiRentalController {

    private final RentalService rentalService;
    private final RentalRepository rentalRepository;
    private final MotorbikeService motorbikeService;

    public ApiRentalController(RentalService rentalService, RentalRepository rentalRepository,
                               MotorbikeService motorbikeService) {
        this.rentalService = rentalService;
        this.rentalRepository = rentalRepository;
        this.motorbikeService = motorbikeService;
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

    @GetMapping("/pending")
    @PreAuthorize("hasRole('lessor')")
    public ResponseEntity<?> getPendingRentals(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Rental> rentals = rentalService.getPendingRentalsByLessor(userDetails.getUser().getUserId());
        return ResponseEntity.ok(rentals);
    }

    @GetMapping("/confirmed")
    @PreAuthorize("hasRole('lessor')")
    public ResponseEntity<?> getConfirmedRentals(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Rental> rentals = rentalService.getConfirmedRentalsByLessor(userDetails.getUser().getUserId());
        return ResponseEntity.ok(rentals);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('lessor')")
    public ResponseEntity<?> getActiveRentals(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Rental> rentals = rentalService.getActiveRentalsByLessor(userDetails.getUser().getUserId());
        return ResponseEntity.ok(rentals);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('lessor')")
    public ResponseEntity<?> getAllRentals(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Rental> rentals = rentalService.getAllRentalsByLessor(userDetails.getUser().getUserId());
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


    @GetMapping("/{rentalId}/renter")
    @PreAuthorize("hasRole('renter')") // hoặc 'renter' nếu cần
    public ResponseEntity<?> getRentalDetailByRenter(
            @PathVariable Integer rentalId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Rental rental = rentalService.getRentalByIdAndRenter(rentalId, userDetails.getUser().getUserId());
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

    @PostMapping("/verify-qr")
    public ResponseEntity<Map<String, Object>> verifyQRCode(@RequestBody QRCodeRequestDTO qrCode) {
        Logger logger = LoggerFactory.getLogger(ApiRentalController.class);
        logger.info("📩 Request received for /qr/verify: rentalId={}, type={}, timestamp={}",
                qrCode.getRentalId(), qrCode.getType(), qrCode.getTimestamp());

        Map<String, Object> response = new HashMap<>();
        try {
            Rental rental = rentalRepository.findById(qrCode.getRentalId())
                    .orElseThrow(() -> new EntityNotFoundException("Rental not found"));
            logger.info("✅ Rental found: {}", rental);

            // Lấy thời gian hiện tại theo UTC
            OffsetDateTime nowUtc = OffsetDateTime.now(ZoneOffset.UTC);
            logger.info("⏰ Current time (UTC): {}", nowUtc);

            // Parse timestamp từ QR code (cũng ở UTC)
            OffsetDateTime qrTimestampUtc = OffsetDateTime.parse(qrCode.getTimestamp())
                    .withOffsetSameInstant(ZoneOffset.UTC);
            logger.info("📌 QR timestamp (UTC): {}", qrTimestampUtc);

            // So sánh chênh lệch
            Duration duration = Duration.between(qrTimestampUtc, nowUtc);
            logger.info("⌛ Time difference: {} minutes", duration.toMinutes());

            if (duration.toMinutes() > 5) {
                throw new IllegalStateException("Mã QR đã hết hạn");
            }

            LocalDate startDate = rental.getStartDate();
            LocalDate endDate = rental.getEndDate();

            if ("pickup".equalsIgnoreCase(qrCode.getType())) {
                logger.info("🚚 Pickup flow: status={}, paymentStatus={}, start={}, end={}, now={}",
                        rental.getStatus(), rental.getPaymentStatus(), startDate, endDate, nowUtc.toLocalDate());
                rental.setStatus(RentalStatus.active);

                if (rental.getRentalContract() == null || rental.getRentalContract().getBike() == null) {
                    throw new IllegalStateException("Rental contract or bike is null");
                }
                BikeStatusUpdateRequest request = new BikeStatusUpdateRequest();
                request.setBikeIds(Collections.singletonList(
                        Long.valueOf(rental.getRentalContract().getBike().getBikeId())));
                request.setStatus("rented");
                logger.info("🔄 Updating bike status: {}", request);
                motorbikeService.updateStatuses(request);

            } else if ("return".equalsIgnoreCase(qrCode.getType())) {
                if (rental.getStatus() != RentalStatus.active || nowUtc.toLocalDate().isAfter(endDate)) {
                    throw new IllegalStateException("Đơn thuê không hợp lệ để trả xe");
                }
                rental.setStatus(RentalStatus.completed);

                if (rental.getRentalContract() == null || rental.getRentalContract().getBike() == null) {
                    throw new IllegalStateException("Rental contract or bike is null");
                }
                BikeStatusUpdateRequest request = new BikeStatusUpdateRequest();
                request.setBikeIds(Collections.singletonList(
                        Long.valueOf(rental.getRentalContract().getBike().getBikeId())));
                request.setStatus("available");
                logger.info("🔄 Updating bike status: {}", request);
                motorbikeService.updateStatuses(request);

            } else {
                throw new IllegalStateException("Loại mã QR không hợp lệ");
            }

            rental.setUpdatedAt(nowUtc.toLocalDateTime());
            rentalRepository.save(rental);

            response.put("success", true);
            response.put("message", "Xác thực mã QR thành công");
            response.put("rental", rental);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("❌ Error processing QR verification: ", e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }



//    @GetMapping("/my")
//    public ResponseEntity<List<Motorbike>> getMotorByUserId(@AuthenticationPrincipal CustomUserDetails userDetails){
//        Integer userIdInt = userDetails.getUser().getUserId();
//        Long myUserId = userIdInt.longValue();
//        return ResponseEntity.ok(motorbikeService.getMotorbikesByUserId(myUserId));
//    }
}
