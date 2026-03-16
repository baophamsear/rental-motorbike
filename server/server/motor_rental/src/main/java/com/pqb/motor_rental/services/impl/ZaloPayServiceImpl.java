package com.pqb.motor_rental.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pqb.motor_rental.entities.Payment;
import com.pqb.motor_rental.entities.Rental;
import com.pqb.motor_rental.enums.PaymentMethod;
import com.pqb.motor_rental.enums.PaymentStatus;
import com.pqb.motor_rental.repositories.PaymentRepository;
import com.pqb.motor_rental.repositories.RentalRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.pqb.motor_rental.util.ZaloPayUtils.hmacSHA256;

@Service
public class ZaloPayServiceImpl {
    private final String appId;
    private final String key1;
    private final String key2;
    private final String callbackUrl;
    private final String queryOrderUrl;

    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;


    public ZaloPayServiceImpl(
            @Value("${zalopay.app_id}") String appId,
            @Value("${zalopay.key1}") String key1,
            @Value("${zalopay.key2}") String key2,
            @Value("${zalopay.callback_url}") String callbackUrl,
            @Value("${zalopay.query_order_url}") String queryOrderUrl,
            PaymentRepository paymentRepository,
            RentalRepository rentalRepository) {
        this.appId = appId;
        this.key1 = key1;
        this.key2 = key2;
        this.callbackUrl = callbackUrl;
        this.queryOrderUrl = queryOrderUrl;
        this.paymentRepository = paymentRepository;
        this.rentalRepository = rentalRepository;
    }

    // Hàm tạo đơn ZaloPay
    public Map<String, Object> createOrder(Long orderId, Long amount, Long userId, String description, String redirectUrl) throws JsonProcessingException {
        // app_trans_id phải có dạng yyMMdd_orderId
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        String appTransId = sdf.format(new Date()) + "_" + orderId;

        long appTime = System.currentTimeMillis();

        Map<String, String> embedMap = new HashMap<>();
        embedMap.put("redirecturl", redirectUrl);
        String embedData = new ObjectMapper().writeValueAsString(embedMap);
        String item = "[]";

        int amountInt = amount.intValue();

        // Chuỗi ký MAC theo tài liệu ZaloPay
        String dataToSign = String.join("|",
                appId,
                appTransId,
                userId.toString(),
                String.valueOf(amountInt),
                String.valueOf(appTime),
                embedData,
                item
        );

        String mac = HMACSHA256(dataToSign, key1);

        Map<String, Object> payload = new HashMap<>();
        payload.put("app_id", Integer.parseInt(appId));
        payload.put("app_trans_id", appTransId);
        payload.put("app_user", userId.toString());
        payload.put("amount", amountInt);
        payload.put("app_time", appTime);
        payload.put("embed_data", embedData);
        payload.put("item", item);
        payload.put("description", description);
        payload.put("mac", mac);

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            String url = "https://sb-openapi.zalopay.vn/v2/create";
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> result = response.getBody();

                // 🔥 add app_trans_id để frontend + DB có thể dùng
                result.put("app_trans_id", appTransId);

                return result;
            } else {
                throw new RuntimeException("Không tạo được đơn hàng ZaloPay: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo order ZaloPay", e);
        }
    }


    public Map<String, Object> checkAndUpdatePayment(String appTransId) throws Exception {
        // Loại bỏ tiền tố không mong muốn (nếu có)
        String cleanedAppTransId = appTransId.replace("appTransId=", "");

        // Tạo MAC
        String data = appId + "|" + cleanedAppTransId + "|" + key1; // Theo tài liệu: appid|apptransid|key1
        String mac = HMACSHA256(data, key1);

        // Log thông tin
        System.out.println("=== DEBUG CHECK STATUS ===");
        System.out.println("appId: " + appId);
        System.out.println("appTransId: " + cleanedAppTransId);
        System.out.println("Data to sign: " + data);
        System.out.println("Generated MAC: " + mac);
        System.out.println("queryOrderUrl: " + queryOrderUrl);

        // Chuẩn bị URL với query params
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(queryOrderUrl)
                .queryParam("appid", appId)
                .queryParam("apptransid", cleanedAppTransId)
                .queryParam("mac", mac);

        // Gọi API
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> responseEntity;
        try {
            responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, null, Map.class);
            System.out.println("ZaloPay response: " + responseEntity.getBody());
        } catch (Exception e) {
            System.out.println("Exception when calling ZaloPay API: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi gọi API ZaloPay: " + e.getMessage(), e);
        }

        Map<String, Object> response = responseEntity.getBody();
        if (response == null) {
            throw new RuntimeException("Phản hồi từ ZaloPay là null");
        }

        Object returnCodeObj = response.get("returncode");
        Integer returnCode = returnCodeObj instanceof Integer ? (Integer) returnCodeObj : null;

        if (returnCode == null) {
            throw new RuntimeException("return_code không hợp lệ: " + response);
        }

        if (returnCode != 1) {
            throw new RuntimeException("Kiểm tra trạng thái ZaloPay thất bại: " + response.get("return_message") +
                    ", sub_return_code: " + response.get("sub_return_code"));
        }


        Payment payment = paymentRepository.findByTxnRef(cleanedAppTransId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy payment với appTransId: " + cleanedAppTransId));


        Object zpTransIdObj = response.get("zptransid");
        String zpTransId = null;
        if (zpTransIdObj != null) {
            zpTransId = zpTransIdObj.toString(); // convert sang String
        }

        payment.setStatus(PaymentStatus.success);
        payment.setPaymentTime(LocalDateTime.now());
        payment.setTransactionNo(zpTransId);

        paymentRepository.save(payment);
        return response;
    }




    private String HMACSHA256(String data, String key) {
        try {
            Mac sha256HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256HMAC.init(secretKey);
            byte[] hash = sha256HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // Convert to hex (lowercase)
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error while generating HMAC SHA256", e);
        }
    }



    public boolean verifyCallback(String data, String reqMac) {
        String calculatedMac = HMACSHA256(data, key2);
        return calculatedMac.equals(reqMac);
    }
}
