package com.pqb.motor_rental.services.impl;

import com.pqb.motor_rental.dto.BikeNotificationDTO;
import com.pqb.motor_rental.entities.*;
import com.pqb.motor_rental.enums.RentalStatus;
import com.pqb.motor_rental.repositories.NotificationRepository;
import com.pqb.motor_rental.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Service
public class NotificationServiceImpl implements NotificationService {


    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Autowired
    private SimpMessagingTemplate messagingTemplate;



    @Override
    public void notifyAdminBikeSubmitted() {
        Map<String, String> payload = new HashMap<>();
        payload.put("message", "New bike submitted");
        messagingTemplate.convertAndSend("/topic/admin-bike-notifications", payload);
    }

    @Override
    public void initContract(RentalContract contract, String notification) {
        messagingTemplate.convertAndSend("/topic/notifications/init-contract" + contract.getLessor().getUserId(), notification);
    }

    @Override
    public void activeContract(RentalContract contract, String notification) {
        messagingTemplate.convertAndSend("/topic/notifications/active-contract" + contract.getLessor().getUserId(), notification);
    }

    @Override
    public void rejectContract(RentalContract contract, String notification) {
        messagingTemplate.convertAndSend("/topic/notifications/reject-contract" + contract.getLessor().getUserId(), notification);
    }

    @Override
    public void createRental(RentalContract contract, Notification notification) {
        messagingTemplate.convertAndSend("/topic/notifications/create-rental" + contract.getLessor().getUserId(), notification);
    }

    @Override
    public void sendNotification(Integer userId, String message, RentalStatus status) {
        // Tạo notification
        Notification noti = new Notification();
        noti.setUserId(userId);
        noti.setMessage(message);
        noti.setTimestamp(LocalDateTime.now());

        // Lưu notification vào database
        notificationRepository.save(noti);

        // Chuẩn bị payload cho WebSocket
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", noti.getId());
        payload.put("message", noti.getMessage());
        payload.put("timestamp", noti.getTimestamp().toString());
        payload.put("userId", noti.getUserId());

        // Gửi thông báo qua WebSocket
        try {
            NotificationTopic topic = NotificationTopic.fromRentalStatus(status);
            messagingTemplate.convertAndSend(topic.getTopic(userId), payload);
        } catch (Exception e) {
            // Có thể thêm logging lỗi nếu cần
        }
    }


}
