package com.pqb.motor_rental.dto;

import com.pqb.motor_rental.entities.LocationPoint;
import com.pqb.motor_rental.enums.ContractStatus;
import com.pqb.motor_rental.enums.PaymentCycle;

import java.time.LocalDate;

public class ContractUpdateRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private PaymentCycle paymentCycle;
    private ContractStatus status;
    private LocationPoint locationPoint;

    public ContractUpdateRequest() {}

    public ContractUpdateRequest(LocalDate startDate, LocalDate endDate, PaymentCycle paymentCycle, ContractStatus status
    , LocationPoint locationPoint) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.paymentCycle = paymentCycle;
        this.status = status;
        this.locationPoint = locationPoint;
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

    public PaymentCycle getPaymentCycle() {
        return paymentCycle;
    }

    public void setPaymentCycle(PaymentCycle paymentCycle) {
        this.paymentCycle = paymentCycle;
    }

    public ContractStatus getStatus() {
        return status;
    }

    public void setStatus(ContractStatus status) {
        this.status = status;
    }

    public LocationPoint getLocationPoint() {
        return locationPoint;
    }

    public void setLocationPoint(LocationPoint locationPoint) {
        this.locationPoint = locationPoint;
    }
}
