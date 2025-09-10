package com.pqb.motor_rental.services.impl;

import com.pqb.motor_rental.dto.BikeNotificationDTO;
import com.pqb.motor_rental.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void notifyAdminBikeSubmitted(BikeNotificationDTO dto) {

        System.out.println("📤 Sending WebSocket message to admin...");
        System.out.println("📨 Bike: " + dto.getBikeName() + " | Owner: " + dto.getOwnerEmail());


        messagingTemplate.convertAndSend("/topic/admin-bike-notifications", dto);
    }
}
