package com.pqb.motor_rental.repositories;

import com.pqb.motor_rental.entities.RentalContract;
import com.pqb.motor_rental.enums.ContractStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContractRepository extends JpaRepository<RentalContract, Long> {
    List<RentalContract> findByLessorUserId(Long lessorId);
    long countByLessor_UserId(Integer lessorId);
    Page<RentalContract> findByStatus(ContractStatus status, Pageable pageable);


    // Lấy danh sách các xe ở gần theo hợp đồng
    @Query(value = """
        SELECT c.*
        FROM rental_contracts c
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
    List<RentalContract> findNearbyContracts(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusKm") double radiusKm
    );
}
