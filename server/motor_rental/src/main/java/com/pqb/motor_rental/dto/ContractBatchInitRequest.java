package com.pqb.motor_rental.dto;

import java.util.List;

// Khởi tạo hợp đồng
public class ContractBatchInitRequest {
    private List<Long> bikeIds;
    private double serviceFee;

    public ContractBatchInitRequest(List<Long> bikeIds, double serviceFee) {
        this.bikeIds = bikeIds;
        this.serviceFee = serviceFee;
    }

    public ContractBatchInitRequest() {}

    public List<Long> getBikeIds() {
        return bikeIds;
    }

    public void setBikeIds(List<Long> bikeIds) {
        this.bikeIds = bikeIds;
    }

    public double getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(double serviceFee) {
        this.serviceFee = serviceFee;
    }
}
