package com.pqb.motor_rental.services;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface ReportService {
    Map<String, Object> getRevenueReport(int year);
}
