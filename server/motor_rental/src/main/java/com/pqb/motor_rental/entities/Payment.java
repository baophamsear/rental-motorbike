package com.pqb.motor_rental.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer paymentId;

    @ManyToOne
    @JoinColumn(name = "rental_id")
    private Rental rental;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private BigDecimal amount;
    private LocalDateTime paymentTime;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
}
