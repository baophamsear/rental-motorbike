package com.pqb.motor_rental.entities;

import com.pqb.motor_rental.enums.PickupType;
import com.pqb.motor_rental.enums.RentalStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "rentals")
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rentalId;

    @ManyToOne
    @JoinColumn(name = "renter_id")
    private User renter;

    @ManyToOne
    @JoinColumn(name = "lessor_id")
    private User lessor;

    @ManyToOne
    @JoinColumn(name = "bike_id")
    private Motorbike bike;

    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "pickup_location_id")
    private Location pickupLocation;

    @ManyToOne
    @JoinColumn(name = "return_location_id")
    private Location returnLocation;

    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private RentalStatus status;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private PickupType pickupType;
}
