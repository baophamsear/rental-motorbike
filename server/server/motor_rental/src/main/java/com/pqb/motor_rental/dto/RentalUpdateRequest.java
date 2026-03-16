package com.pqb.motor_rental.dto;

import java.time.LocalDate;

public class RentalUpdateRequest {
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double totalAmount;

    public RentalUpdateRequest(String status, LocalDate startDate, LocalDate endDate, Double totalAmount) {
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalAmount = totalAmount;
    }

    public RentalUpdateRequest() {}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
