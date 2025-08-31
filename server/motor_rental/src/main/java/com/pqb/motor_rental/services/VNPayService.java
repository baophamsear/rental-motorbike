package com.pqb.motor_rental.services;

import com.pqb.motor_rental.dto.PaymentDTO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface VNPayService {
    PaymentDTO.VNPayResponse createVnPayPayment(HttpServletRequest request, long amount, String orderInfo);
}

