package com.pqb.motor_rental.services.impl;

import com.pqb.motor_rental.entities.Motorbike;
import com.pqb.motor_rental.entities.User;
import com.pqb.motor_rental.enums.BikeStatus;
import com.pqb.motor_rental.repositories.MotorbikeRepository;
import com.pqb.motor_rental.repositories.UserRepository;
import com.pqb.motor_rental.services.MotorbikeService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class MotorbikeServiceImpl implements MotorbikeService {

    private final MotorbikeRepository motorbikeRepository;
    private final UserRepository userRepository;

    public MotorbikeServiceImpl(MotorbikeRepository motorbikeRepository, UserRepository userRepository) {
        this.motorbikeRepository = motorbikeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void createMotorbike(Motorbike motorbike, String email) {
        User lessor = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if (!lessor.getRole().name().equals("lessor")) {
            throw new RuntimeException("Chỉ người cho thuê mới được đăng xe");
        }

        Motorbike mtbk = new Motorbike();
        mtbk.setName(motorbike.getName());
        mtbk.setBrand(motorbike.getBrand());
        mtbk.setOwner(lessor);
        mtbk.setLicensePlate(motorbike.getLicensePlate());
        mtbk.setImageUrl(motorbike.getImageUrl());
        mtbk.setPricePerDay(motorbike.getPricePerDay());
        mtbk.setLocation(motorbike.getLocation());
        mtbk.setStatus(BikeStatus.pending);
        motorbikeRepository.save(mtbk);
    }

    @Override
    public List<Motorbike> getAllMotorbikes() {
        return motorbikeRepository.findAll();
    }

    // Tìm tất cả các xe nào mà nó có status là pending
    @Override
    public List<Motorbike> findByStatus(BikeStatus status) {
        return motorbikeRepository.findByStatus(status);
    }

    @Override
    public Motorbike approveMotorbike(Long motorbikeId) {
        Motorbike motorbike = motorbikeRepository.findById(motorbikeId)
                .orElseThrow(() -> new RuntimeException("Motorbike not found"));

        if (motorbike.getStatus() != BikeStatus.pending) {
            throw new IllegalStateException("Motorbike is not in pending status");
        }

        motorbike.setStatus(BikeStatus.available);
        return motorbikeRepository.save(motorbike);
    }

    @Override
    public Motorbike findById(Long motorbikeId) {
        return motorbikeRepository.findById(motorbikeId)
                .orElseThrow(() -> new RuntimeException("Motorbike not found"));
    }

}
