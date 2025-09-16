package com.pqb.motor_rental.services.impl;

import com.pqb.motor_rental.dto.BikeStatusUpdateRequest;
import com.pqb.motor_rental.entities.Motorbike;
import com.pqb.motor_rental.entities.User;
import com.pqb.motor_rental.enums.BikeStatus;
import com.pqb.motor_rental.enums.ContractStatus;
import com.pqb.motor_rental.repositories.MotorbikeRepository;
import com.pqb.motor_rental.repositories.UserRepository;
import com.pqb.motor_rental.services.MotorbikeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
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
    public Motorbike createMotorbike(Motorbike motorbike, String email) {
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
        mtbk.setLocation(motorbike.getLocation());
        mtbk.setStatus(BikeStatus.pending);
        motorbikeRepository.save(mtbk);
        return mtbk;
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

    @Override
    public List<Motorbike> getMotorbikesByUserId(Long userId) {
        return motorbikeRepository.findByOwner_UserId(userId);
    }

    @Override
    public void updateStatuses(BikeStatusUpdateRequest request) {
        List<Motorbike> bikes = motorbikeRepository.findAllById((request.getBikeIds()));
        for (Motorbike motorbike: bikes) {
            motorbike.setStatus(BikeStatus.valueOf(request.getStatus()));
            if ("rejected".equals(request.getStatus()) && request.getRejectionReason() != null) {
                motorbike.setNote(request.getRejectionReason());
            }
        }
        motorbikeRepository.saveAll(bikes);
    }

    @Override
    public void updateMotorAvailable(Long motorbikeId) {
        Motorbike bike = motorbikeRepository.findById(motorbikeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe tương ứng!"));

        if (bike.getStatus() == BikeStatus.available) {
            throw new IllegalStateException("Xe đang hoạt động!");
        }

        bike.setStatus(BikeStatus.available);
        motorbikeRepository.save(bike);
    }

    @Override
    public List<Motorbike> getAvailableMotorbikes() {
        return motorbikeRepository.findByStatus(BikeStatus.available);
    }

    @Override
    public List<Motorbike> getMotorbikesNearby(double lat, double lng, double radiusKm) {
        return motorbikeRepository. findNearbyMotorbikes(lat, lng, radiusKm);
    }

    @Override
    public Page<Motorbike> getMotorbikesByOwner(Integer ownerId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by("bikeId").descending());
        return motorbikeRepository.findByOwner_UserId(ownerId, pageable);
    }

    @Override
    public Motorbike getMotorbikeForOwner(Integer bikeId, Integer ownerId) {
        return motorbikeRepository.findByBikeIdAndOwner_UserId(bikeId, ownerId)
                .orElseThrow(() -> new AccessDeniedException("Bạn không có quyền xem thông tin xe này hoặc xe không tồn tại"));
    }




}
