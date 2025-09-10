package com.pqb.motor_rental.services;

import com.pqb.motor_rental.dto.BikeNotificationDTO;
import org.springframework.stereotype.Service;


public interface NotificationService {
    void notifyAdminBikeSubmitted(BikeNotificationDTO dto);
}
