package com.pqb.motor_rental.controllers.api;


import com.pqb.motor_rental.dto.ContractBatchInitRequest;
import com.pqb.motor_rental.dto.ContractUpdateRequest;
import com.pqb.motor_rental.entities.Motorbike;
import com.pqb.motor_rental.entities.RentalContract;
import com.pqb.motor_rental.entities.User;
import com.pqb.motor_rental.security.CustomUserDetails;
import com.pqb.motor_rental.services.ContractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contracts")
public class ApiContractController {

    private static final Logger log = LoggerFactory.getLogger(ApiContractController.class);
    private final ContractService contractService;
    public ApiContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @PostMapping("/init-multiple")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> initMultiple(@RequestBody ContractBatchInitRequest req) {
        List<RentalContract> created = contractService.createMultipleDrafts(req.getBikeIds());
        return ResponseEntity.ok(Map.of(
                "message", "Đã tạo " + created.size() + " hợp đồng",
                "createdContracts", created.stream().map(c -> Map.of(
                        "contractId", c.getContractId(),
                        "bikeId", c.getBike().getBikeId(),
                        "lessorId", c.getLessor().getUserId()
                )).toList()
        ));
    }

    @GetMapping("/mine")
    public ResponseEntity<?> getMyContracts(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<RentalContract> contracts = contractService.getContractsByUserId(userDetails.getUser().getUserId());
        return ResponseEntity.ok(contracts);
    }

    @PatchMapping("/{id}/update")
    @PreAuthorize("hasRole('lessor')")
    public ResponseEntity<?> updateContractByUser(
            @PathVariable Long id,
            @RequestBody ContractUpdateRequest dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        contractService.updateContractFromLessor(id, dto, userDetails.getUser().getUserId());
        return ResponseEntity.ok("Cập nhật hợp đồng thành công.");
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> getAllContracts() {
        List<RentalContract> contracts = contractService.getAllContracts();
        return ResponseEntity.ok(contracts);
    }

    @PatchMapping("/{id}/active")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> updateContractActived(@PathVariable Long id) {
        contractService.updateActiveContractStatus(id);
        return ResponseEntity.ok("Cập nhật hợp đồng thành công!");
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('renter')")
    public ResponseEntity<List<RentalContract>> getActiveContracts(Principal principal) {
        return ResponseEntity.ok(contractService.getActiveContracts());
    }

    @GetMapping("/nearby")
    public ResponseEntity<?> getNearbyContracts(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "5") double radiusKm
    ) {
        List<RentalContract> contracts = contractService.getContractsNearby(lat, lng, radiusKm);

        return ResponseEntity.ok(contracts);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createContract(@RequestBody RentalContract rentalContract) {
        RentalContract contract = contractService.createContract(rentalContract);
        return ResponseEntity.ok("Contract created successfully and is pending!");
    }

    @GetMapping("/pending")
    public ResponseEntity<List<RentalContract>> getPendingContract() {
        List<RentalContract> contracts = contractService.getPendingContracts();
        return ResponseEntity.ok(contracts);
    }

    public ResponseEntity<String> approveContract(@PathVariable Long id, Authentication authentication) {

        // Lấy thông tin admin từ token
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User admin = userDetails.getUser();

        RentalContract approved = contractService.approveContract(id, admin);
        return ResponseEntity.ok("Contract approved successfully");
    }

}
