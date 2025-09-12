package com.pqb.motor_rental.services.impl;

import com.pqb.motor_rental.dto.BikeNotificationDTO;
import com.pqb.motor_rental.entities.Motorbike;
import com.pqb.motor_rental.entities.RentalContract;
import com.pqb.motor_rental.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class NotificationServiceImpl implements NotificationService {

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
}
