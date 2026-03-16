package com.pqb.motor_rental.services;

import com.pqb.motor_rental.dto.BikeNotificationDTO;
import com.pqb.motor_rental.entities.*;
import com.pqb.motor_rental.enums.RentalStatus;
import org.springframework.stereotype.Service;


public interface NotificationService {
    void notifyAdminBikeSubmitted();

    // khởi thông báo khi admin duyệt xe xong
    void initContract(RentalContract contract, String notification);

    void activeContract(RentalContract contract, String notification);

    void rejectContract(RentalContract contract, String notification);

    void createRental(RentalContract contract, Notification notification);

    void sendNotification(Integer userId, String message, RentalStatus status);
}
