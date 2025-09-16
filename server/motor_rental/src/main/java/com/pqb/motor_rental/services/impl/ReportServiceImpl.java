package com.pqb.motor_rental.services.impl;

import com.pqb.motor_rental.repositories.RentalRepository;
import com.pqb.motor_rental.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class ReportServiceImpl implements ReportService {

    private final RentalRepository rentalRepository;

    public ReportServiceImpl (RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    private static final Map<Long, String> BRAND_MAP = new HashMap<>();
    private static final Map<Long, String> LOCATION_MAP = new HashMap<>();

    static {
        BRAND_MAP.put(1L, "Honda");
        BRAND_MAP.put(2L, "Yamaha");
        BRAND_MAP.put(3L, "Suzuki");
        LOCATION_MAP.put(1L, "TP. HCM");
        LOCATION_MAP.put(2L, "Đà Nẵng");
        LOCATION_MAP.put(3L, "Hà Nội");
    }

    @Override
    public Map<String, Object> getRevenueReport(int year) {
        Map<String, Object> result = new HashMap<>();

        // Doanh thu theo tháng
        List<Object[]> revenueByMonth = rentalRepository.findRevenueByYearAndMonth();
        List<String> months = Arrays.asList("Th1", "Th2", "Th3", "Th4", "Th5", "Th6", "Th7", "Th8", "Th9", "Th10", "Th11", "Th12");
        List<BigDecimal> monthlyRevenue = new ArrayList<>(Collections.nCopies(12, BigDecimal.ZERO));

        for (Object[] row : revenueByMonth) {
            int rowYear = ((Number) row[0]).intValue();
            if (rowYear == year) {
                int month = ((Number) row[1]).intValue() - 1;
                BigDecimal revenue = row[2] != null
                        ? BigDecimal.valueOf(((Number) row[2]).doubleValue())
                        : BigDecimal.ZERO;
                monthlyRevenue.set(month, revenue);
            }
        }

        // Doanh thu theo hãng xe
        List<Object[]> revenueByBrand = rentalRepository.findRevenueByBrand(year);
        Map<String, BigDecimal> brandRevenue = new HashMap<>();
        for (Object[] row : revenueByBrand) {
            Long brandId = ((Number) row[0]).longValue();
            BigDecimal revenue = row[1] != null
                    ? BigDecimal.valueOf(((Number) row[1]).doubleValue())
                    : BigDecimal.ZERO;
            brandRevenue.put(BRAND_MAP.getOrDefault(brandId, "Unknown"), revenue);
        }

        // Doanh thu theo địa điểm
        List<Object[]> revenueByLocation = rentalRepository.findRevenueByLocation(year);
        Map<String, BigDecimal> locationRevenue = new HashMap<>();
        for (Object[] row : revenueByLocation) {
            Long locationId = ((Number) row[0]).longValue();
            BigDecimal revenue = row[1] != null
                    ? BigDecimal.valueOf(((Number) row[1]).doubleValue())
                    : BigDecimal.ZERO;
            locationRevenue.put(LOCATION_MAP.getOrDefault(locationId, "Unknown"), revenue);
        }

        // Thống kê tổng quan
        List<Object[]> summaryList = rentalRepository.findSummaryStats();
        Object[] summary = summaryList.getFirst(); // chỉ có 1 record
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRevenue", summary[0] != null ?
                BigDecimal.valueOf(((Number) summary[0]).doubleValue()) : BigDecimal.ZERO);
        stats.put("totalOrders", summary[1] != null ? summary[1] : 0L);
        stats.put("totalUsers", summary[2] != null ? summary[2] : 0L);
        stats.put("avgRevenue", summary[3] != null ?
                BigDecimal.valueOf(((Number) summary[3]).doubleValue()) : BigDecimal.ZERO);

        // Chuẩn bị dữ liệu cho frontend
        result.put("lineData", Map.of(
                "labels", months,
                "datasets", List.of(Map.of(
                        "label", "Doanh thu (VND) năm " + year,
                        "data", monthlyRevenue,
                        "borderColor", "#7c3aed",
                        "backgroundColor", "rgba(124, 58, 237, 0.1)",
                        "tension", 0.4,
                        "fill", true
                ))
        ));
        result.put("brandBarData", Map.of(
                "labels", new ArrayList<>(brandRevenue.keySet()),
                "datasets", List.of(Map.of(
                        "label", "Doanh thu theo hãng xe năm " + year,
                        "data", new ArrayList<>(brandRevenue.values()),
                        "backgroundColor", List.of("rgba(124, 58, 237, 0.8)", "rgba(168, 85, 247, 0.8)", "rgba(236, 72, 153, 0.8)"),
                        "borderColor", "#7c3aed",
                        "borderWidth", 1
                ))
        ));
        result.put("locationPieData", Map.of(
                "labels", new ArrayList<>(locationRevenue.keySet()),
                "datasets", List.of(Map.of(
                        "data", new ArrayList<>(locationRevenue.values()),
                        "backgroundColor", List.of("#f59e0b", "#10b981", "#ef4444"),
                        "borderWidth", 2,
                        "borderColor", "#ffffff"
                ))
        ));
        result.put("statData", stats);

        return result;
    }
}
