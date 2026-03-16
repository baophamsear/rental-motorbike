package com.pqb.motor_rental.repositories;

import com.pqb.motor_rental.entities.Rental;
import com.pqb.motor_rental.enums.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findByRenter_UserId(Long userId);
    List<Rental> findByStatusAndRentalContract_Lessor_UserId(RentalStatus status, Integer userId);
    List<Rental> findByRentalContract_Lessor_UserId(Integer userId);



    // Lấy danh sách các rental theo chủ xe
    @Query("""
    SELECT r FROM Rental r
    WHERE r.rentalId = :rentalId
      AND r.rentalContract.lessor.userId = :userId
    """)
    Optional<Rental> findByIdAndLessor(@Param("rentalId") Integer rentalId, @Param("userId") Integer userId);


    // Lấy danh sách người dùng theo renter (người thuê)
    @Query("""
    SELECT r FROM Rental r
    WHERE r.rentalId = :rentalId
      AND r.renter.userId = :userId
    """)
    Optional<Rental> findByIdAndRenter(@Param("rentalId") Integer rentalId, @Param("userId") Integer userId);

    // Thống kê doanh thu hàng năm
    @Query("SELECT YEAR(r.startDate), MONTH(r.startDate), SUM(r.totalPrice) " +
            "FROM Rental r " +
            "WHERE r.startDate IS NOT NULL " +
            "AND r.status IN (com.pqb.motor_rental.enums.RentalStatus.completed, com.pqb.motor_rental.enums.RentalStatus.active) " +
            "GROUP BY YEAR(r.startDate), MONTH(r.startDate) " +
            "ORDER BY YEAR(r.startDate), MONTH(r.startDate)")
    List<Object[]> findRevenueByYearAndMonth();


    // Thống kê theo hãng xe
    @Query("SELECT b.brand.brandId, SUM(r.totalPrice) AS totalRevenue " +
            "FROM Rental r JOIN RentalContract rc ON r.rentalContract.contractId = rc.contractId " +
            "JOIN Motorbike b ON rc.bike.bikeId = b.bikeId " +
            "WHERE r.status IN (com.pqb.motor_rental.enums.RentalStatus.completed, com.pqb.motor_rental.enums.RentalStatus.active) " +
            "AND YEAR(r.startDate) = :year AND r.startDate IS NOT NULL " +
            "GROUP BY b.brand.brandId " +
            "ORDER BY SUM(r.totalPrice) DESC")
    List<Object[]> findRevenueByBrand(@Param("year") int year);

    // Thống kê doanh thu theo địa điểm
    @Query("SELECT b.location.locationId AS locationId, SUM(r.totalPrice) AS totalRevenue " +
            "FROM Rental r JOIN RentalContract rc ON r.rentalId = rc.bike.bikeId " +
            "JOIN Motorbike b ON rc.bike.bikeId = b.bikeId " +
            "WHERE r.status IN (com.pqb.motor_rental.enums.RentalStatus.completed, com.pqb.motor_rental.enums.RentalStatus.active) " +
            "AND YEAR(r.startDate) = :year AND r.startDate IS NOT NULL " +
            "GROUP BY b.location.locationId")
    List<Object[]> findRevenueByLocation(@Param("year") int year);


    // Thống kê tổng quan
    @Query("SELECT SUM(r.totalPrice) AS totalRevenue, COUNT(DISTINCT r.rentalId) AS totalOrders, " +
            "COUNT(DISTINCT r.rentalId) AS totalUsers, AVG(r.totalPrice) AS avgRevenue " +
            "FROM Rental r WHERE r.status IN (com.pqb.motor_rental.enums.RentalStatus.completed, com.pqb.motor_rental.enums.RentalStatus.active)")
    List<Object[]> findSummaryStats();

    // Lấy tất cả các rental chủ xe có (phân trang)
    Page<Rental> findByRentalContract_Lessor_UserId(Integer lessorId, Pageable pageable);

    // Lấy theo trạng thái (chủ xe)
    Page<Rental> findByStatusAndRentalContract_Lessor_UserId(
            RentalStatus status,
            Integer lessorId,
            Pageable pageable
    );







}
