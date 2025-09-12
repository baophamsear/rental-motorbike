package com.pqb.motor_rental.controllers.api;

import com.pqb.motor_rental.config.VNPayConfig;
import com.pqb.motor_rental.dto.PaymentDTO;
import com.pqb.motor_rental.entities.Payment;
import com.pqb.motor_rental.enums.PaymentStatus;
import com.pqb.motor_rental.repositories.PaymentRepository;
import com.pqb.motor_rental.services.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.pqb.motor_rental.util.VNPayUtil.hmacSHA512;

@RestController
@RequestMapping("/api/vnpay")
public class ApiVNPayController {

    private final VNPayConfig vnPayConfig;
    private final PaymentRepository paymentRepository;
    private final VNPayService vnPayService;

    public ApiVNPayController(VNPayConfig vnPayConfig, PaymentRepository paymentRepository, VNPayService vnPayService) {
        this.vnPayConfig = vnPayConfig;
        this.paymentRepository = paymentRepository;
        this.vnPayService = vnPayService;
    }

    @GetMapping("/callback")
    public ResponseEntity<String> handleVNPayCallback(HttpServletRequest request) throws UnsupportedEncodingException {
        Map<String, String> fields = new HashMap<>();

        // 1. Lấy tất cả param bắt đầu bằng "vnp_"
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String param = params.nextElement();
            String value = request.getParameter(param);
            if (param.startsWith("vnp_")) {
                fields.put(param, value);
            }
        }

        // 2. Xác minh chữ ký
        String receivedHash = fields.remove("vnp_SecureHash");

        List<String> sortedKeys = new ArrayList<>(fields.keySet());
        Collections.sort(sortedKeys);

        StringBuilder data = new StringBuilder();
        for (String key : sortedKeys) {
            data.append(key).append("=").append(URLEncoder.encode(fields.get(key), StandardCharsets.US_ASCII.name())).append("&");
        }
        data.setLength(data.length() - 1);

        String calculatedHash = hmacSHA512(vnPayConfig.getSecretKey(), data.toString());

        if (!calculatedHash.equals(receivedHash)) {
            return ResponseEntity.badRequest().body("Sai chữ ký – giao dịch không hợp lệ!");
        }

        // 3. Xử lý kết quả thanh toán
        String responseCode = fields.get("vnp_ResponseCode");
        String txnRef = fields.get("vnp_TxnRef");

        Optional<Payment> optional = paymentRepository.findByTxnRef(txnRef);
        if (optional.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy giao dịch với mã: " + txnRef);
        }

        Payment payment = optional.get();

        if ("00".equals(responseCode)) {
            payment.setStatus(PaymentStatus.success);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            payment.setPaymentTime(LocalDateTime.parse(fields.get("vnp_PayDate"), formatter));

            payment.setTransactionNo(fields.get("vnp_TransactionNo"));
        } else {
            payment.setStatus(PaymentStatus.failed);
        }

        paymentRepository.save(payment);
        return ResponseEntity.ok("Xử lý callback VNPay thành công.");
    }

    @PostMapping(value = "/create-payment")
    public ResponseEntity<PaymentDTO.VNPayResponse> createPayment(
            @RequestParam long amount,
            @RequestParam String orderInfo,
            HttpServletRequest request,
            @RequestParam(required = false) String bankCode
    ) {
        // gọi service để tạo URL thanh toán
        PaymentDTO.VNPayResponse response = vnPayService.createVnPayPayment(request, amount, orderInfo, bankCode);

        return ResponseEntity.ok(response);
    }
}

