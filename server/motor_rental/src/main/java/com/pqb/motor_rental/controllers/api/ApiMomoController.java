package com.pqb.motor_rental.controllers.api;

import com.pqb.motor_rental.config.MoMoConfig;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/momo")
public class ApiMomoController {

    @PostMapping("/create-payment")
    public ResponseEntity<?> createMomoPayment(@RequestParam long amount) throws Exception {
        String orderId = UUID.randomUUID().toString();
        String requestId = UUID.randomUUID().toString();
        String orderInfo = "Thanh toán dịch vụ thuê xe";
        String requestType = "captureWallet";

        // Chuỗi raw để ký
        String rawData = "accessKey=" + MoMoConfig.ACCESS_KEY +
                "&amount=" + amount +
                "&extraData=" +
                "&ipnUrl=" + MoMoConfig.IPN_URL +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + MoMoConfig.PARTNER_CODE +
                "&redirectUrl=" + MoMoConfig.REDIRECT_URL +
                "&requestId=" + requestId +
                "&requestType=" + requestType;

        String signature = hmacSHA256(MoMoConfig.SECRET_KEY, rawData);

        Map<String, Object> body = new HashMap<>();
        body.put("partnerCode", MoMoConfig.PARTNER_CODE);
        body.put("accessKey", MoMoConfig.ACCESS_KEY);
        body.put("requestId", requestId);
        body.put("amount", amount);
        body.put("orderId", orderId);
        body.put("orderInfo", orderInfo);
        body.put("redirectUrl", MoMoConfig.REDIRECT_URL);
        body.put("ipnUrl", MoMoConfig.IPN_URL);
        body.put("extraData", "");
        body.put("requestType", requestType);
        body.put("signature", signature);
        body.put("lang", "vi");

        // Gửi request
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> momoResponse = restTemplate.postForEntity(MoMoConfig.ENDPOINT, entity, Map.class);
        return ResponseEntity.ok(momoResponse.getBody());
    }

    private String hmacSHA256(String key, String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secretKeySpec);
        byte[] result = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(result);
    }

    @PostMapping("/callback")
    public ResponseEntity<?> handleCallback(@RequestBody Map<String, Object> payload) {
        System.out.println("✅ Momo callback received: " + payload);

        // TODO: kiểm tra "signature" và cập nhật trạng thái hợp đồng theo `orderId`

        return ResponseEntity.ok("OK");
    }


}
