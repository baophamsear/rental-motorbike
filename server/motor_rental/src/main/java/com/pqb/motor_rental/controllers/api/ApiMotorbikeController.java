package com.pqb.motor_rental.controllers.api;

import com.pqb.motor_rental.entities.Motorbike;
import com.pqb.motor_rental.enums.BikeStatus;
import com.pqb.motor_rental.services.CloudinaryService;
import com.pqb.motor_rental.services.MotorbikeService;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/bikes")
public class ApiMotorbikeController {

    private final MotorbikeService motorbikeService;
    private final CloudinaryService cloudinaryService;

    public ApiMotorbikeController(MotorbikeService motorbikeService, CloudinaryService cloudinaryService) {
        this.motorbikeService = motorbikeService;
        this.cloudinaryService = cloudinaryService;
    }

    // Phương thức đẩy thông tin của 1 xe lên
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> postBike(
            @RequestBody Motorbike motorbike,
            @RequestPart("motorImages") List<MultipartFile> motorImages,
            @RequestPart("licenseImages") List<MultipartFile> licenseImages,
            Principal principal) {
        try {
            String email = principal.getName(); // Lấy email từ token

            List<String> motorImageUrls = new ArrayList<>();
            for (MultipartFile file : motorImages) {
                motorImageUrls.add(cloudinaryService.uploadImage(file));
            }

            List<String> licenseImageUrls = new ArrayList<>();
            for (MultipartFile file : licenseImages) {
                licenseImageUrls.add(cloudinaryService.uploadImage(file));
            }

            motorbike.setImageUrl(motorImageUrls);
            motorbike.setLicensePlate(licenseImageUrls);

            motorbike.setStatus(BikeStatus.pending);

            motorbikeService.createMotorbike(motorbike, email);
            return ResponseEntity.ok("Đăng xe thành công");
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi đăng xe: " + e.getMessage());
        }
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

