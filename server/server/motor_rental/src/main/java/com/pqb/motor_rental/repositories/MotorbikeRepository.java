package com.pqb.motor_rental.repositories;

import com.pqb.motor_rental.entities.Motorbike;
import com.pqb.motor_rental.enums.BikeStatus;
import com.pqb.motor_rental.enums.ContractStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MotorbikeRepository extends JpaRepository<Motorbike, Long> {
    List<Motorbike> findByStatus(BikeStatus status);
    List<Motorbike> findByOwner_UserId(Long userId);
    long countByOwner_UserId(Integer owner);
    Page<Motorbike> findByOwner_UserId(Integer ownerId, Pageable pageable);
    Optional<Motorbike> findByBikeIdAndOwner_UserId(Integer bikeId, Integer ownerId);
    List<Motorbike> findByLocation_LocationId(Long locationId);


    @Query(value = """
        SELECT m.*
        FROM motorbike m
        JOIN rental_contracts c ON m.bike_id = c.bike_id
        JOIN location_point l ON c.location_id = l.id
        WHERE (
            6371 * acos(
                cos(radians(:lat)) * cos(radians(l.latitude)) *
                cos(radians(l.longitude) - radians(:lng)) +
                sin(radians(:lat)) * sin(radians(l.latitude))
            )
        ) < :radiusKm
        AND c.status = 'active'
        """, nativeQuery = true)
    List<Motorbike> findNearbyMotorbikes(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusKm") double radiusKm
    );
}
