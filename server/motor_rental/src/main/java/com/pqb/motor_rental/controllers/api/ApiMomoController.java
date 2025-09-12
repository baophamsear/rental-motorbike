package com.pqb.motor_rental.controllers.api;

import com.pqb.motor_rental.config.MoMoConfig;
import com.pqb.motor_rental.entities.Payment;
import com.pqb.motor_rental.entities.Rental;
import com.pqb.motor_rental.enums.PaymentMethod;
import com.pqb.motor_rental.enums.PaymentStatus;
import com.pqb.motor_rental.repositories.PaymentRepository;
import com.pqb.motor_rental.repositories.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/momo")
public class ApiMomoController {

    @Autowired
    RentalRepository rentalRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @PostMapping("/create-payment")
    public ResponseEntity<?> createMomoPayment(@RequestBody Map<String, Object> payload) throws Exception {

        long amount = Long.parseLong(payload.get("amount").toString());

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
        StringBuilder sb = new StringBuilder();
        for (byte b : result) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    @PostMapping("/callback")
    public ResponseEntity<?> handleCallback(@RequestBody Map<String, Object> payload) {
        System.out.println("✅ Momo callback received: " + payload);

        // ✅ Lấy giá trị đúng kiểu an toàn
        String orderIdStr = String.valueOf(payload.get("orderId"));
        String transId = String.valueOf(payload.get("transId"));
        String amountStr = String.valueOf(payload.get("amount"));
        String message = String.valueOf(payload.get("message"));
        String resultCodeStr = String.valueOf(payload.get("resultCode"));
        String orderInfo = String.valueOf(payload.get("orderInfo"));

        // ✅ Convert orderId và amount về kiểu Long
        Long rentalId = Long.parseLong(orderIdStr);
        Double amount = Double.parseDouble(amountStr);

        // ✅ Tìm rental tương ứng
        Optional<Rental> rentalOptional = rentalRepository.findById(rentalId);
        if (rentalOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy rental với ID = " + rentalId);
        }

        Rental rental = rentalOptional.get();

        // ✅ Tạo và lưu đối tượng Payment
        Payment payment = new Payment();
        payment.setRental(rental);
        payment.setPaymentMethod(PaymentMethod.momo);
        payment.setAmount(amount);
        payment.setPaymentTime(LocalDateTime.now());
        payment.setStatus(resultCodeStr.equals("0") ? PaymentStatus.success : PaymentStatus.failed);
        payment.setTransactionNo(transId);
        payment.setTxnRef(orderIdStr);

        paymentRepository.save(payment);

        return ResponseEntity.ok("✅ Callback received and payment saved.");
    }



}
