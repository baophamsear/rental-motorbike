package com.pqb.motor_rental.controllers.api;

import com.pqb.motor_rental.services.ReportService;
import com.pqb.motor_rental.services.impl.ReportServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ApiReportController {

    private final ReportServiceImpl reportServiceImpl;

    public ApiReportController(ReportServiceImpl reportServiceImpl) {
        this.reportServiceImpl = reportServiceImpl;
    }

    @GetMapping("/revenue")
    public Map<String, Object> getRevenueReport(@RequestParam(defaultValue = "2025") int year) {
        return reportServiceImpl.getRevenueReport(year);
    }
}
