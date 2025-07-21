package com.pqb.motor_rental.services.impl;

import com.pqb.motor_rental.entities.Motorbike;
import com.pqb.motor_rental.entities.User;
import com.pqb.motor_rental.repositories.MotorbikeRepository;
import com.pqb.motor_rental.repositories.UserRepository;
import com.pqb.motor_rental.services.MotorbikeService;
import org.springframework.stereotype.Service;

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

        if (!lessor.getRole().name().equals("LESSOR")) {
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
        motorbikeRepository.save(mtbk);
    }

}
