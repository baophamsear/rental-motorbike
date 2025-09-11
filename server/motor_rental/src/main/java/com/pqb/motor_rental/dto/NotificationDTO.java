package com.pqb.motor_rental.dto;

import java.time.LocalDateTime;

public class NotificationDTO {
    private Integer userId;
    private String message;
    private LocalDateTime timestamp;

    // Constructor mặc định (yêu cầu bởi Jackson)
    public NotificationDTO() {
    }

    // Constructor với các trường
    public NotificationDTO(Integer userId, String message, LocalDateTime timestamp) {
        this.userId = userId;
        this.message = message;
        this.timestamp = timestamp;
    }

    // Getter và Setter
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
