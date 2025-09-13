package com.pqb.motor_rental.entities;

import com.pqb.motor_rental.enums.RentalStatus;

public enum NotificationTopic {
    CONFIRMED_RENTAL("/topic/notifications/confirm-rental"),
    REJECT_RENTAL("/topic/notifications/cancel-rental"),
    ACTIVE_RENTAL("/topic/notifications/cancel-rental"),
    COMPLETED_RENTAL("/topic/notifications/completed-rental");

    private final String topic;

    NotificationTopic(String topic) {
        this.topic = topic;
    }

    public String getTopic(Integer userId) {
        return this.topic + userId;
    }

    public static NotificationTopic fromRentalStatus(RentalStatus status) {
        switch (status) {
            case confirmed:
                return CONFIRMED_RENTAL;
            case cancelled:
                return REJECT_RENTAL;
            case active:
                return ACTIVE_RENTAL;
            case completed:
                return COMPLETED_RENTAL;
            default:
                throw new IllegalArgumentException("No topic defined for status: " + status);
        }
    }
}
