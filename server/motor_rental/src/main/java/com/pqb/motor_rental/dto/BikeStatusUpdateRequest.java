package com.pqb.motor_rental.dto;

import java.util.List;

// Cập nhật trạng thái xe
public class BikeStatusUpdateRequest {
    private List<Long> bikeIds;
    private String status;
    private String rejectionReason;

    public BikeStatusUpdateRequest(List<Long> bikeIds, String status, String rejectionReason) {
        this.bikeIds = bikeIds;
        this.status = status;
        this.rejectionReason = rejectionReason;
    }

    public BikeStatusUpdateRequest(List<Long> bikeIds, String status) {
        this.bikeIds = bikeIds;
        this.status = status;
    }

    public BikeStatusUpdateRequest() {}

    public List<Long> getBikeIds() {
        return bikeIds;
    }

    public void setBikeIds(List<Long> bikeIds) {
        this.bikeIds = bikeIds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
