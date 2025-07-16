package com.pqb.motor_rental.entities;

import com.pqb.motor_rental.enums.BikeStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "motorbike")
public class Motorbike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bikeId;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    private String licensePlate;
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private BikeStatus status;

    private BigDecimal pricePerDay;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    private Boolean isHomeDelivery;
}
