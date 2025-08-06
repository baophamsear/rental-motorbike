package com.pqb.motor_rental.controllers.api;

import com.pqb.motor_rental.entities.RentalContract;
import com.pqb.motor_rental.entities.User;
import com.pqb.motor_rental.security.CustomUserDetails;
import com.pqb.motor_rental.services.ContractService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
public class ApiRentalContractController {

    private final ContractService contractService;

    public ApiRentalContractController(ContractService contractService) {
        this.contractService = contractService;
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
