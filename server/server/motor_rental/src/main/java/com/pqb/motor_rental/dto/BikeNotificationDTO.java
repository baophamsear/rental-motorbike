package com.pqb.motor_rental.dto;

// NO USE
public class BikeNotificationDTO {
    private Integer bikeId;
    private String bikeName;
    private String ownerEmail;
    private String status;

    public BikeNotificationDTO() {}

    public BikeNotificationDTO(Integer bikeId, String bikeName, String ownerEmail, String status) {
        this.bikeId = bikeId;
        this.bikeName = bikeName;
        this.ownerEmail = ownerEmail;
        this.status = status;
    }

    public Integer getBikeId() {
        return bikeId;
    }

    public void setBikeId(Integer bikeId) {
        this.bikeId = bikeId;
    }

    public String getBikeName() {
        return bikeName;
    }

    public void setBikeName(String bikeName) {
        this.bikeName = bikeName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
