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

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBikeId() {
        return bikeId;
    }

    public void setBikeId(Integer bikeId) {
        this.bikeId = bikeId;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BikeStatus getStatus() {
        return status;
    }

    public void setStatus(BikeStatus status) {
        this.status = status;
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Boolean getHomeDelivery() {
        return isHomeDelivery;
    }

    public void setHomeDelivery(Boolean homeDelivery) {
        isHomeDelivery = homeDelivery;
    }
}
