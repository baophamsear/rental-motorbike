package com.pqb.motor_rental.controllers.api;

import com.pqb.motor_rental.entities.Motorbike;
import com.pqb.motor_rental.services.MotorbikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/bikes")
public class ApiMotorbikeController {

    private final MotorbikeService motorbikeService;

    public ApiMotorbikeController(MotorbikeService motorbikeService) {
        this.motorbikeService = motorbikeService;
    }

    @PostMapping
    public ResponseEntity<String> postBike(@RequestBody Motorbike motorbike,
                                           Principal principal) {
        String email = principal.getName(); // Lấy email từ token
        motorbikeService.createMotorbike(motorbike, email);
        return ResponseEntity.ok("Đăng xe thành công");
    }
}

