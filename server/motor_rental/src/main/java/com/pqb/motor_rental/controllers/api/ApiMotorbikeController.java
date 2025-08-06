package com.pqb.motor_rental.controllers.api;

import com.pqb.motor_rental.entities.Motorbike;
import com.pqb.motor_rental.enums.BikeStatus;
import com.pqb.motor_rental.services.MotorbikeService;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/bikes")
public class ApiMotorbikeController {

    private final MotorbikeService motorbikeService;

    public ApiMotorbikeController(MotorbikeService motorbikeService) {
        this.motorbikeService = motorbikeService;
    }

    // Phương thức đẩy thông tin của 1 xe lên
    @PostMapping
    public ResponseEntity<String> postBike(@RequestBody Motorbike motorbike,
                                           Principal principal) {
        String email = principal.getName(); // Lấy email từ token
        motorbikeService.createMotorbike(motorbike, email);
        return ResponseEntity.ok("Đăng xe thành công");
    }

    // Phương thức lấy tất cả
    @GetMapping
    public ResponseEntity<List<Motorbike>> getBike(Principal principal) {
        return ResponseEntity.ok(motorbikeService.getAllMotorbikes());
    }

    // Phương thức lấy những xe nào mà nó có giá trị status là "pending"
    @GetMapping("/pending")
    public ResponseEntity<List<Motorbike>> getPendingMotorbikes(){
        List<Motorbike> motorbikeList = motorbikeService.findByStatus(BikeStatus.pending);
        return ResponseEntity.ok(motorbikeList);
    }


    // Phương thức để cập nhật lại trạng thái của motorbike
    @PatchMapping("/{id}/approve")
    public ResponseEntity<String> approveMotorbike(@PathVariable Long id){
        Motorbike approved = motorbikeService.approveMotorbike(id);
        return ResponseEntity.ok("Motorbike " + approved.toString() + " approved successfully.");
    }
}

