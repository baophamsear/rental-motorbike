package com.pqb.motor_rental.controllers.api;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pqb.motor_rental.dto.ContractBatchInitRequest;
import com.pqb.motor_rental.dto.ContractUpdateRequest;
import com.pqb.motor_rental.entities.Motorbike;
import com.pqb.motor_rental.entities.Notification;
import com.pqb.motor_rental.entities.RentalContract;
import com.pqb.motor_rental.entities.User;
import com.pqb.motor_rental.repositories.ContractRepository;
import com.pqb.motor_rental.repositories.NotificationRepository;
import com.pqb.motor_rental.repositories.UserRepository;
import com.pqb.motor_rental.security.CustomUserDetails;
import com.pqb.motor_rental.services.ContractService;
import com.pqb.motor_rental.services.NotificationService;
import com.pqb.motor_rental.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contracts")
public class ApiContractController {

    private static final Logger log = LoggerFactory.getLogger(ApiContractController.class);

    private final ContractService contractService;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate; // Thêm để gửi qua WebSocket
    private final RestTemplate restTemplate; // Thay OkHttpClient bằng RestTemplate
    private final ObjectMapper objectMapper; // Để serialize JSON
    private final ContractRepository contractRepository;

    @Autowired
    public ApiContractController(
            ContractService contractService,
            NotificationRepository notificationRepository,
            NotificationService notificationService,
            UserRepository userRepository,
            SimpMessagingTemplate messagingTemplate,
            RestTemplate restTemplate,
            ObjectMapper objectMapper, ContractRepository contractRepository) {
        this.contractService = contractService;
        this.notificationRepository = notificationRepository;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.contractRepository = contractRepository;
    }

    @PostMapping("/init-multiple")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> initMultiple(@RequestBody ContractBatchInitRequest req) throws JsonProcessingException {
        log.info("Received request to init contracts for bikeIds: {}", req.getBikeIds());
        List<RentalContract> created;
        try {
            created = contractService.createMultipleDrafts(req.getBikeIds());
            log.info("Created {} contracts", created.size());
        } catch (Exception e) {
            log.error("Error creating contracts: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Failed to create contracts: " + e.getMessage()));
        }

        for (RentalContract contract : created) {
            Notification notification = new Notification();
            notification.setUserId(contract.getLessor().getUserId());
            notification.setMessage("Hợp đồng cho xe của bạn (" + contract.getBike().getName() + ") đã khởi tạo thành công! " +
                    "Vui lòng cập nhật thông tin và gởi lại!" +
                    "Hãy nhanh chóng cập nhật thông tin!");
            notification.setTimestamp(LocalDateTime.now());

            try {
                notificationRepository.save(notification);
                log.info("Saved notification to repository: {}", notification.getMessage());
            } catch (Exception e) {
                log.error("Error saving notification: {}", e.getMessage());
            }

            Map<String, Object> payload = new HashMap<>();
            payload.put("id", notification.getId());
            payload.put("message", notification.getMessage());
            payload.put("timestamp", notification.getTimestamp().toString());
            payload.put("userId", notification.getUserId());

            String topic = "/topic/notifications/init-contract" + contract.getLessor().getUserId();
            try {
                messagingTemplate.convertAndSend(topic, payload);
                log.info("Sent notification to {}: {}", topic, payload);
            } catch (Exception e) {
                log.error("Error sending notification to {}: {}", topic, e.getMessage());
            }
        }

        return ResponseEntity.ok(Map.of(
                "message", "Đã tạo " + created.size() + " hợp đồng",
                "createdContracts", created.stream().map(c -> Map.of(
                        "contractId", c.getContractId(),
                        "bikeId", c.getBike().getBikeId(),
                        "lessorId", c.getLessor().getUserId()
                )).toList()
        ));
    }

    @GetMapping("/mine")
    public ResponseEntity<?> getMyContracts(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<RentalContract> contracts = contractService.getContractsByUserId(userDetails.getUser().getUserId());
        return ResponseEntity.ok(contracts);
    }



    @PatchMapping("/{id}/update")
    @PreAuthorize("hasRole('lessor')")
    public ResponseEntity<?> updateContractByUser(
            @PathVariable Long id,
            @RequestBody ContractUpdateRequest dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        contractService.updateContractFromLessor(id, dto, userDetails.getUser().getUserId());
        return ResponseEntity.ok("Cập nhật hợp đồng thành công.");
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> getAllContracts() {
        List<RentalContract> contracts = contractService.getAllContracts();
        return ResponseEntity.ok(contracts);
    }

    @PatchMapping("/{id}/active")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> updateContractActived(@PathVariable Long id) {
        contractService.updateActiveContractStatus(id);

        RentalContract contract = contractRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng!"));

        Notification noti = new Notification();
        noti.setUserId(contract.getLessor().getUserId());
        noti.setMessage("Hợp đồng cho xe của bạn (Xe: " + contract.getBike().getName() + ") đã được duyệt thành công!");
        noti.setTimestamp(LocalDateTime.now());

        try {
            notificationRepository.save(noti);
            log.info("Saved notification to repository: {}", noti.getMessage());
        } catch (Exception e) {
            log.error("Error saving notification: {}", e.getMessage());
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", noti.getId());
        payload.put("message", noti.getMessage());
        payload.put("timestamp", noti.getTimestamp().toString());
        payload.put("userId", noti.getUserId());

        String topic = "/topic/notifications/active-contract" + contract.getLessor().getUserId();
        try {
            messagingTemplate.convertAndSend(topic, payload);
            log.info("Sent notification to {}: {}", topic, payload);
        } catch (Exception e) {
            log.error("Error sending notification to {}: {}", topic, e.getMessage());
        }

        return ResponseEntity.ok("Cập nhật hợp đồng thành công!");
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('renter')")
    public ResponseEntity<List<RentalContract>> getActiveContracts(Principal principal) {
        return ResponseEntity.ok(contractService.getActiveContracts());
    }

    @GetMapping("/nearby")
    public ResponseEntity<?> getNearbyContracts(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "5") double radiusKm
    ) {
        List<RentalContract> contracts = contractService.getContractsNearby(lat, lng, radiusKm);

        return ResponseEntity.ok(contracts);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createContract(@RequestBody RentalContract rentalContract) {
        RentalContract contract = contractService.createContract(rentalContract);
        return ResponseEntity.ok("Contract created successfully and is pending!");
    }

    @GetMapping("/pending")
    public ResponseEntity<List<RentalContract>> getPendingContract() {
        List<RentalContract> contracts = contractService.getPendingContracts();
        return ResponseEntity.ok(contracts);
    }

    @PatchMapping("/{contractId}/reject")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> rejectContract(
            @PathVariable Long contractId,
            @RequestBody Map<String, String> body) {

        String rejectReason = body.get("rejectReason");
        if (rejectReason == null || rejectReason.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Lý do từ chối không được để trống");
        }

        // Cập nhật trạng thái reject
        RentalContract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng!"));

        contractService.rejectContract(contractId, rejectReason);

        // Tạo notification
        Notification noti = new Notification();
        noti.setUserId(contract.getLessor().getUserId());
        noti.setMessage("Hợp đồng cho xe của bạn (Xe: " + contract.getBike().getName() +
                ") đã bị từ chối. Lý do: " + rejectReason);
        noti.setTimestamp(LocalDateTime.now());

        try {
            notificationRepository.save(noti);
            log.info("Saved notification to repository: {}", noti.getMessage());
        } catch (Exception e) {
            log.error("Error saving notification: {}", e.getMessage());
        }

        // Chuẩn bị payload cho WebSocket
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", noti.getId());
        payload.put("message", noti.getMessage());
        payload.put("timestamp", noti.getTimestamp().toString());
        payload.put("userId", noti.getUserId());

        // Chủ đề reject riêng biệt
        String topic = "/topic/notifications/reject-contract" + contract.getLessor().getUserId();
        try {
            messagingTemplate.convertAndSend(topic, payload);
            log.info("Sent reject notification to {}: {}", topic, payload);
        } catch (Exception e) {
            log.error("Error sending reject notification to {}: {}", topic, e.getMessage());
        }

        return ResponseEntity.ok("Hợp đồng đã bị từ chối với lý do: " + rejectReason);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('lessor')")
    public ResponseEntity<?> getContractById(@PathVariable Long id, Authentication authentication) {
        RentalContract contract = contractService.getContractById(id);

        // 🔒 Bảo mật: chỉ lessor, renter trong hợp đồng hoặc admin mới xem được
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer userId = userDetails.getUser().getUserId();

        if (!contract.getBike().getOwner().getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Bạn không có quyền truy cập hợp đồng này");
        }

        return ResponseEntity.ok(contract);
    }


}
