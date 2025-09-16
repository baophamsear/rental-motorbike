package com.pqb.motor_rental.repositories;

import com.pqb.motor_rental.entities.Rental;
import com.pqb.motor_rental.entities.Motorbike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReportRepository extends JpaRepository<Rental, Long> {
    // Thống kê doanh thu theo năm và tháng
    @Query("SELECT YEAR(r.startDate) AS year, MONTH(r.startDate) AS month, SUM(r.totalPrice) AS totalRevenue " +
            "FROM Rental r WHERE r.status IN (com.pqb.motor_rental.enums.RentalStatus.completed, com.pqb.motor_rental.enums.RentalStatus.active) " +
            "GROUP BY YEAR(r.startDate), MONTH(r.startDate) " +
            "ORDER BY YEAR(r.startDate), MONTH(r.startDate)")
    List<Object[]> findRevenueByYearAndMonth();

    @Query("SELECT b.brand.brandId, SUM(r.totalPrice) AS totalRevenue " +
            "FROM Rental r JOIN RentalContract rc ON r.rentalContract.contractId = rc.contractId " +
            "JOIN Motorbike b ON rc.bike.bikeId = b.bikeId " +
            "WHERE r.status IN (com.pqb.motor_rental.enums.RentalStatus.completed, com.pqb.motor_rental.enums.RentalStatus.active) " +
            "GROUP BY b.brand.brandId " +
            "ORDER BY SUM(r.totalPrice) DESC")
    List<Object[]> findRevenueByBrand();

    // Thống kê doanh thu theo địa điểm
    @Query("SELECT b.location.locationId AS locationId, SUM(r.totalPrice) AS totalRevenue " +
            "FROM Rental r JOIN RentalContract rc ON r.rentalId = rc.bike.bikeId " +
            "JOIN Motorbike b ON rc.bike.bikeId = b.bikeId " +
            "WHERE r.status IN (com.pqb.motor_rental.enums.RentalStatus.completed, com.pqb.motor_rental.enums.RentalStatus.active) " +
            "GROUP BY b.location.locationId")
    List<Object[]> findRevenueByLocation();

    // Thống kê tổng quan
    @Query("SELECT SUM(r.totalPrice) AS totalRevenue, COUNT(DISTINCT r.rentalId) AS totalOrders, " +
            "COUNT(DISTINCT r.rentalId) AS totalUsers, AVG(r.totalPrice) AS avgRevenue " +
            "FROM Rental r WHERE r.status IN (com.pqb.motor_rental.enums.RentalStatus.completed, com.pqb.motor_rental.enums.RentalStatus.active)")
    Object[] findSummaryStats();
}
