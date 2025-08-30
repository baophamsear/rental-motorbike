package com.pqb.motor_rental.entities;

import com.pqb.motor_rental.enums.BikeStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Column(columnDefinition = "TEXT")
    private String licensePlate;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private BikeStatus status;

    private Double pricePerDay = 100000.0;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    private Boolean isHomeDelivery;

    private String name;

    private String note;



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

    public List<String> getLicensePlate() {
        if (licensePlate == null || licensePlate.isEmpty()) return new ArrayList<>();
        return Arrays.asList(licensePlate.split(","));
    }

    public void setLicensePlate(List<String> images) {
        this.licensePlate = String.join(",", images);
    }

    public List<String> getImageUrl() {
        if (imageUrl == null || imageUrl.isEmpty()) return new ArrayList<>();
        return Arrays.asList(imageUrl.split(","));
    }

    public void setImageUrl(List<String> images) {
        this.imageUrl = String.join(",", images);
    }

    public BikeStatus getStatus() {
        return status;
    }

    public void setStatus(BikeStatus status) {
        this.status = status;
    }

    public Double getPricePerDay() {
        return pricePerDay;
    }

//    public void setPricePerDay() {
//        this.pricePerDay = 100000.0;
//    }

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }


}
