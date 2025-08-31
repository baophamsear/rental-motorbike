package com.pqb.motor_rental.services.impl;

import com.pqb.motor_rental.config.VNPayConfig;
import com.pqb.motor_rental.dto.PaymentDTO;
import com.pqb.motor_rental.services.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VNPayServiceImpl implements VNPayService {

    private final VNPayConfig vnPayConfig;

    public VNPayServiceImpl(VNPayConfig vnPayConfig) {
        this.vnPayConfig = vnPayConfig;
    }

    @Override
    public PaymentDTO.VNPayResponse createVnPayPayment(HttpServletRequest request, long amount, String orderInfo) {
        // 1. Sinh txnRef
        String txnRef = "INV" + System.currentTimeMillis();

        // 2. Tạo params từ config
        Map<String, String> params = vnPayConfig.getVNPayConfig(orderInfo, txnRef);
        params.put("vnp_Amount", String.valueOf(amount * 100));
        params.put("vnp_IpAddr", getClientIp(request));

        // 3. Sắp xếp và ký hash
        List<String> sortedKeys = new ArrayList<>(params.keySet());
        Collections.sort(sortedKeys);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (String key : sortedKeys) {
            String value = params.get(key);
            if (value != null && !value.isEmpty()) {
                hashData.append(key).append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII)).append('&');
                query.append(key).append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII)).append('&');
            }
        }

        hashData.setLength(hashData.length() - 1);
        query.setLength(query.length() - 1);

        String secureHash = hmacSHA512(vnPayConfig.getSecretKey(), hashData.toString());
        query.append("&vnp_SecureHash=").append(secureHash);

        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + query;

        // 4. Trả về DTO
        return new PaymentDTO.VNPayResponse(
                paymentUrl,
                txnRef,
                params.get("vnp_ExpireDate")
        );
    }


    // IP Helper
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    // Ký HMAC SHA512
    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac.init(secretKey);
            byte[] bytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi ký HMAC", e);
        }
    }
}
