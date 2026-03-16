package com.pqb.motor_rental.controllers.api;

import com.pqb.motor_rental.dto.NotificationDTO;
import com.pqb.motor_rental.entities.Notification;
import com.pqb.motor_rental.repositories.NotificationRepository;
import com.pqb.motor_rental.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class ApiNotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @PostMapping
    public ResponseEntity<Notification> createNotification(
            @RequestBody NotificationDTO notificationDTO) {

        Notification notification = new Notification(
                notificationDTO.getUserId(),
                notificationDTO.getMessage(),
                notificationDTO.getTimestamp()
        );
        notification = notificationRepository.save(notification);
        return ResponseEntity.ok(notification);
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUser().getUserId();
        List<Notification> notifications = notificationRepository.findByUserIdOrderByTimestampDesc(userId);
        return ResponseEntity.ok(notifications);
    }





//    @DeleteMapping("/clear")
//    public ResponseEntity<Void> clearNotifications(Authentication authentication) {
//        String userId = authentication.getName();
//        notificationRepository.deleteAll(notificationRepository.findByUserIdOrderByTimestampDesc(userId));
//        return ResponseEntity.ok().build();
//    }
}
