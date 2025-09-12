package com.pqb.motor_rental.repositories;

import com.pqb.motor_rental.entities.Rental;
import com.pqb.motor_rental.enums.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findByRenter_UserId(Long userId);
    List<Rental> findByStatusAndRentalContract_Lessor_UserId(RentalStatus status, Integer userId);
    List<Rental> findByRentalContract_Lessor_UserId(Integer userId);

    @Query("""
    SELECT r FROM Rental r
    WHERE r.rentalId = :rentalId
      AND r.rentalContract.lessor.userId = :userId
    """)
    Optional<Rental> findByIdAndLessor(@Param("rentalId") Integer rentalId, @Param("userId") Integer userId);


    @Query("""
    SELECT r FROM Rental r
    WHERE r.rentalId = :rentalId
      AND r.renter.userId = :userId
    """)
    Optional<Rental> findByIdAndRenter(@Param("rentalId") Integer rentalId, @Param("userId") Integer userId);





}
