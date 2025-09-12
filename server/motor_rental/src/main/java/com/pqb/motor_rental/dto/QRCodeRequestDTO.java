package com.pqb.motor_rental.dto;

public class QRCodeRequestDTO {
    private Long rentalId;
    private String type;
    private String timestamp;

    // Getters and setters
    public Long getRentalId() { return rentalId; }
    public void setRentalId(Long rentalId) { this.rentalId = rentalId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
