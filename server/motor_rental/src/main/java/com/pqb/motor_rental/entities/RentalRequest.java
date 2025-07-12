package com.pqb.motor_rental.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "rental_request")
public class RentalRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer requestId;

    @OneToOne
    @JoinColumn(name = "rental_id")
    private Rental rental;

    private Boolean lessorConfirmed;
    private Boolean renterConfirmed;
    private LocalDateTime confirmedAt;
}
