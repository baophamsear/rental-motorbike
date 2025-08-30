package com.pqb.motor_rental.entities;

import com.pqb.motor_rental.enums.ContractStatus;
import com.pqb.motor_rental.enums.PaymentCycle;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "rental_contracts")
public class RentalContract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contract_id")
    private Integer contractId;

    @ManyToOne
    @JoinColumn(name = "lessor_id")
    private User lessor;

    @ManyToOne
    @JoinColumn(name = "bike_id")
    private Motorbike bike;

    private Double serviceFee;

    @Enumerated(EnumType.STRING)
    private PaymentCycle paymentCycle;


    @Column(name = "start_date", nullable = true)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = true)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    private LocalDateTime cancelRequestedAt;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    private LocalDateTime approvedAt;

    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private LocationPoint location;


    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getContractId() {
        return contractId;
    }

    public void setContractId(Integer contractId) {
        this.contractId = contractId;
    }

    public User getLessor() {
        return lessor;
    }

    public void setLessor(User lessor) {
        this.lessor = lessor;
    }

    public Motorbike getBike() {
        return bike;
    }

    public void setBike(Motorbike bike) {
        this.bike = bike;
    }

    public Double getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(Double serviceFee) {
        this.serviceFee = serviceFee;
    }

    public PaymentCycle getPaymentCycle() {
        return paymentCycle;
    }

    public void setPaymentCycle(PaymentCycle paymentCycle) {
        this.paymentCycle = paymentCycle;
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

    public ContractStatus getStatus() {
        return status;
    }

    public void setStatus(ContractStatus status) {
        this.status = status;
    }

    public LocalDateTime getCancelRequestedAt() {
        return cancelRequestedAt;
    }

    public void setCancelRequestedAt(LocalDateTime cancelRequestedAt) {
        this.cancelRequestedAt = cancelRequestedAt;
    }

    public User getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public LocationPoint getLocation() {
        return location;
    }

    public void setLocation(LocationPoint location) {
        this.location = location;
    }
}

