package com.pqb.motor_rental.dto;

import java.time.LocalDate;

public class RentalRequest {
    private Long contractId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double totalPrice;

    public RentalRequest(Long contractId, LocalDate startDate, LocalDate endDate, Double totalPrice) {
        this.contractId = contractId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
    }

    public RentalRequest() {}

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
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

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
