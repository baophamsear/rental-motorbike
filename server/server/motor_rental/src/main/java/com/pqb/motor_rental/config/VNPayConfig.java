package com.pqb.motor_rental.config;

import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Configuration
public class VNPayConfig {
    private final String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private final String vnp_ReturnUrl = "http://localhost:8080/api/vnpay/callback";
    private final String vnp_TmnCode = "ZM585D1R";
    private final String secretKey = "CU8JMGWI9UFYSZH8ZB7W5ZQ22OSZPGFE";
    private final String vnp_Version = "2.1.0";
    private final String vnp_Command = "pay";
    private final String orderType = "other";

    public String getVnp_PayUrl() {
        return vnp_PayUrl;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public Map<String, String> getVNPayConfig(String orderInfo, String txnRef) {
        Map<String, String> vnpParamsMap = new HashMap<>();

        vnpParamsMap.put("vnp_Version", vnp_Version);
        vnpParamsMap.put("vnp_Command", vnp_Command);
        vnpParamsMap.put("vnp_TmnCode", vnp_TmnCode);
        vnpParamsMap.put("vnp_CurrCode", "VND");
        vnpParamsMap.put("vnp_Locale", "vn");
        vnpParamsMap.put("vnp_OrderType", orderType);
        vnpParamsMap.put("vnp_ReturnUrl", vnp_ReturnUrl);
//        vnpParamsMap.put("vnp_BankCode", "NCB");

        vnpParamsMap.put("vnp_TxnRef", txnRef);
        vnpParamsMap.put("vnp_OrderInfo", orderInfo);


        // Dùng LocalDateTime và định dạng chuẩn yyyyMMddHHmmss
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        // Múi giờ GMT+7 (VN)
        ZoneId zoneVN = ZoneId.of("Asia/Ho_Chi_Minh");

        LocalDateTime now = LocalDateTime.now(zoneVN);
        vnpParamsMap.put("vnp_CreateDate", now.format(formatter));

        LocalDateTime expire = now.plusMinutes(15);
        vnpParamsMap.put("vnp_ExpireDate", expire.format(formatter));

        return vnpParamsMap;
    }

}
