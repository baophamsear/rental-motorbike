package com.pqb.motor_rental.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pqb.motor_rental.enums.PickupType;
import com.pqb.motor_rental.enums.RentalPaymentStatus;
import com.pqb.motor_rental.enums.RentalStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "rental")
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rentalId;

    @ManyToOne
    @JoinColumn(name = "renter_id")
    private User renter;

//    @ManyToOne
//    @JoinColumn(name = "lessor_id")
//    private User lessor;
//
//    @ManyToOne
//    @JoinColumn(name = "bike_id")
//    private Motorbike bike;

    @ManyToOne
    @JoinColumn(name = "rental_contract_id")
//    @JsonBackReference(value = "contract-rentals")
    private RentalContract rentalContract;




    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "pickup_location_id")
    private Location pickupLocation;

    @ManyToOne
    @JoinColumn(name = "return_location_id")
    private Location returnLocation;

    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    private RentalStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private PickupType pickupType;

    @OneToOne(mappedBy = "rental", cascade = CascadeType.ALL)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    private RentalPaymentStatus paymentStatus;

    public Rental(Integer rentalId, User renter, RentalContract rentalContract, LocalDate startDate, LocalDate endDate,
                  Location pickupLocation, Location returnLocation, Double totalPrice, RentalStatus status, LocalDateTime createdAt,
                  PickupType pickupType, Payment payment, RentalPaymentStatus paymentStatus) {
        this.rentalId = rentalId;
        this.renter = renter;
        this.rentalContract = rentalContract;
        this.startDate = startDate;
        this.endDate = endDate;
        this.pickupLocation = pickupLocation;
        this.returnLocation = returnLocation;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
        this.pickupType = pickupType;
        this.payment = payment;
        this.paymentStatus = paymentStatus;
    }

    public Rental() {}

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public RentalPaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(RentalPaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Integer getRentalId() {
        return rentalId;
    }

    public void setRentalId(Integer rentalId) {
        this.rentalId = rentalId;
    }

    public User getRenter() {
        return renter;
    }

    public void setRenter(User renter) {
        this.renter = renter;
    }

    public RentalContract getRentalContract() {
        return rentalContract;
    }

    public void setRentalContract(RentalContract rentalContract) {
        this.rentalContract = rentalContract;
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

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(Location pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public Location getReturnLocation() {
        return returnLocation;
    }

    public void setReturnLocation(Location returnLocation) {
        this.returnLocation = returnLocation;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public void setStatus(RentalStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public PickupType getPickupType() {
        return pickupType;
    }

    public void setPickupType(PickupType pickupType) {
        this.pickupType = pickupType;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
