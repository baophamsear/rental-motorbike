    package com.pqb.motor_rental.controllers.api;

    import com.fasterxml.jackson.core.JsonProcessingException;
    import com.fasterxml.jackson.core.type.TypeReference;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import com.pqb.motor_rental.dto.CreateOrderRequest;
    import com.pqb.motor_rental.entities.Payment;
    import com.pqb.motor_rental.entities.Rental;
    import com.pqb.motor_rental.enums.PaymentMethod;
    import com.pqb.motor_rental.enums.PaymentStatus;
    import com.pqb.motor_rental.repositories.PaymentRepository;
    import com.pqb.motor_rental.repositories.RentalRepository;
    import com.pqb.motor_rental.services.ZaloPayService;
    import com.pqb.motor_rental.services.impl.ZaloPayServiceImpl;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.time.LocalDateTime;
    import java.util.Map;

    @RestController
    @RequestMapping("/api/zalopay")
    public class ApiZaloPayController {

        private final ZaloPayServiceImpl zaloPayService;
        private final PaymentRepository paymentRepository;
        private final RentalRepository rentalRepository;

        public ApiZaloPayController(ZaloPayServiceImpl zaloPayService,
                                    PaymentRepository paymentRepository,
                                    RentalRepository rentalRepository) {
            this.zaloPayService = zaloPayService;
            this.paymentRepository = paymentRepository;
            this.rentalRepository = rentalRepository;
        }

        @PostMapping("/create-order")
        public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest request) throws JsonProcessingException {
            Map<String, Object> order = zaloPayService.createOrder(
                    request.getOrderId(),
                    request.getAmount(),
                    request.getUserId(),
                    "Thanh toán thuê xe",
                    "myapp://payment-success"
            );

            Rental rental = rentalRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Rental not found"));

            Payment payment = new Payment();
            payment.setRental(rental);
            payment.setPaymentMethod(PaymentMethod.zalopay);
            payment.setAmount(request.getAmount());
            payment.setStatus(PaymentStatus.pending);
            payment.setTxnRef((String) order.get("app_trans_id"));
            paymentRepository.save(payment);

            return ResponseEntity.ok(order);
        }

        @GetMapping("/checkstatus/{appTransId}")
        public Map<String, Object> checkStatus(@PathVariable String appTransId) {
            try {
                return zaloPayService.checkAndUpdatePayment(appTransId);
            } catch (Exception e) {
                return Map.of("error", e.getMessage());
            }
        }


        @PostMapping("/callback")
        public ResponseEntity<?> callback(@RequestBody Map<String, Object> callbackData) throws Exception {
            String data = (String) callbackData.get("data");
            String reqMac = (String) callbackData.get("mac");

            boolean isValid = zaloPayService.verifyCallback(data, reqMac);
            if (!isValid) return ResponseEntity.badRequest().body("Invalid callback signature");

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> dataMap = mapper.readValue(data, new TypeReference<>() {});
            String appTransId = (String) dataMap.get("app_trans_id");
            String zpTransId = (String) dataMap.get("zp_trans_id");

            Payment payment = paymentRepository.findByTxnRef(appTransId)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            payment.setStatus(PaymentStatus.success);
            payment.setTransactionNo(zpTransId);
            payment.setPaymentTime(LocalDateTime.now());
            paymentRepository.save(payment);

            return ResponseEntity.ok("OK");
        }
    }

