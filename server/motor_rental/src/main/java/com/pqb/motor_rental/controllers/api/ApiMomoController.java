package com.pqb.motor_rental.controllers.api;

import com.pqb.motor_rental.config.MoMoConfig;
import com.pqb.motor_rental.entities.Payment;
import com.pqb.motor_rental.entities.Rental;
import com.pqb.motor_rental.enums.PaymentMethod;
import com.pqb.motor_rental.enums.PaymentStatus;
import com.pqb.motor_rental.repositories.PaymentRepository;
import com.pqb.motor_rental.repositories.RentalRepository;
import com.pqb.motor_rental.services.impl.MotorbikeServiceImpl;
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

        // Kiểm tra chữ ký (signature) nếu muốn

        String orderId = (String) payload.get("orderId");
        String transId = String.valueOf(payload.get("transId"));
        String amount = String.valueOf(payload.get("amount"));
        String message = (String) payload.get("message");
        String resultCode = String.valueOf(payload.get("resultCode")); // 0 = Thành công
        String orderInfo = (String) payload.get("orderInfo");

        String rentalId = orderId; // giả sử orderId = rentalId (nếu bạn gán theo lúc tạo payment)

        Long rentalIdLong = Long.parseLong(orderId); // giả sử orderId chính là rentalId


        Optional<Rental> rentalOptional = rentalRepository.findById(rentalIdLong);
        if (rentalOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy rental với ID = " + rentalIdLong);
        }

        Rental rental = rentalOptional.get();

        // Lưu vào DB
        Payment payment = new Payment();
        payment.setRental(rental);
        payment.setPaymentMethod(PaymentMethod.momo);
        payment.setAmount(Double.parseDouble(amount));
        payment.setPaymentTime(LocalDateTime.now());
        payment.setStatus(resultCode.equals("0") ? PaymentStatus.success : PaymentStatus.failed);
        payment.setTransactionNo(transId);
        payment.setTxnRef(orderId);

        paymentRepository.save(payment);

        return ResponseEntity.ok("Callback received and saved.");
    }


}
