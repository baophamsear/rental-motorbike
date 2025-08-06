package com.pqb.motor_rental.entities;

import com.pqb.motor_rental.enums.PaymentMethod;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_logs")
public class PaymentLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer logId;

    @ManyToOne
    @JoinColumn(name = "lessor_id")
    private User lessor;

    @ManyToOne
    @JoinColumn(name = "rental_id")
    private Rental rental;

    @ManyToOne
    @JoinColumn(name = "contract_id", referencedColumnName = "contractId")
    private RentalContract contract;

    private BigDecimal grossAmount;
    private BigDecimal serviceFee;
    private BigDecimal netAmount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private LocalDateTime paymentTime = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "paid_by")
    private User paidBy;


}

