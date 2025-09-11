package com.pqb.motor_rental.controllers.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pqb.motor_rental.dto.BikeNotificationDTO;
import com.pqb.motor_rental.dto.BikeStatusUpdateRequest;
import com.pqb.motor_rental.entities.Motorbike;
import com.pqb.motor_rental.enums.BikeStatus;
import com.pqb.motor_rental.security.CustomUserDetails;
import com.pqb.motor_rental.services.CloudinaryService;
import com.pqb.motor_rental.services.MotorbikeService;
import com.pqb.motor_rental.services.NotificationService;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/bikes")
public class ApiMotorbikeController {

    @Autowired
    private NotificationService notificationService;

    private static final Logger log = LoggerFactory.getLogger(ApiMotorbikeController.class);
    private final MotorbikeService motorbikeService;
    private final CloudinaryService cloudinaryService;

    public ApiMotorbikeController(MotorbikeService motorbikeService, CloudinaryService cloudinaryService) {
        this.motorbikeService = motorbikeService;
        this.cloudinaryService = cloudinaryService;
    }

    // Phương thức đẩy thông tin của 1 xe lên
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> postBike(
            @RequestPart("motorbike") String motorbikeJson,
            @RequestPart("motorImages") List<MultipartFile> motorImages,
            @RequestPart("licenseImages") List<MultipartFile> licenseImages,
            Principal principal) {
        try {
            String  email = principal.getName(); // Lấy email từ token
            ObjectMapper mapper = new ObjectMapper();
            Motorbike motorbike = mapper.readValue(motorbikeJson, Motorbike.class);

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

            notificationService.notifyAdminBikeSubmitted();
            System.out.println("✅ Đã tạo xe thành công, chuẩn bị gửi noti tới admin...");


            return ResponseEntity.ok("Đăng xe thành công");
        }catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid motorbike data: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi đăng xe: " + e.getMessage());
        }
    }

    // Phương thức lấy tất cả chỉ dành cho admin
    @GetMapping("/alls")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<Motorbike>> getBike(Principal principal) {
        return ResponseEntity.ok(motorbikeService.getAllMotorbikes());
    }

    // Phương thức lấy những xe nào mà nó có giá trị status là "pending"
    @GetMapping("/pending")
    @PreAuthorize("hasRole('admin')")
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

    // Lấy danh sách các motor của 1 user
    @GetMapping("/my")
    public ResponseEntity<List<Motorbike>> getMotorByUserId(@AuthenticationPrincipal CustomUserDetails userDetails){
        Integer userIdInt = userDetails.getUser().getUserId();
        Long myUserId = userIdInt.longValue();
        return ResponseEntity.ok(motorbikeService.getMotorbikesByUserId(myUserId));
    }

    @PatchMapping("/status")
    @PreAuthorize("hasRole('admin')")
//    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<?> updateBikesStatus(@RequestBody BikeStatusUpdateRequest request){
        motorbikeService.updateStatuses(request);
        return ResponseEntity.ok("Updated " + request.getBikeIds().size() + " motorbikes.");
    }

    @PatchMapping("/{id}/available")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> updateBikesAvailableStatus(@PathVariable Long id){
        motorbikeService.updateMotorAvailable(id);
        return ResponseEntity.ok("Updated " + id + " motorbikes.");
    }

    @GetMapping("/available")
    @PreAuthorize("hasRole('renter')")
    public ResponseEntity<List<Motorbike>> getAvailableMotorbikes(Principal principal){
        return ResponseEntity.ok(motorbikeService.getAllMotorbikes());
    }


    @GetMapping("/nearby")
    public ResponseEntity<List<Motorbike>> getNearbyMotorbikes(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "5") double radiusKm
    ) {
        List<Motorbike> bikes = motorbikeService.getMotorbikesNearby(lat, lng, radiusKm);
        return ResponseEntity.ok(bikes);
    }



}

