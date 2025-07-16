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
    private Integer contractId;

    @ManyToOne
    @JoinColumn(name = "lessor_id")
    private User lessor;

    @ManyToOne
    @JoinColumn(name = "bike_id")
    private Motorbike bike;

    private BigDecimal serviceFee;

    @Enumerated(EnumType.STRING)
    private PaymentCycle paymentCycle;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    private LocalDateTime cancelRequestedAt;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    private LocalDateTime approvedAt;
}

