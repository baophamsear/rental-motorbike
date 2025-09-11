package com.pqb.motor_rental.controllers.api;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pqb.motor_rental.dto.ContractBatchInitRequest;
import com.pqb.motor_rental.dto.ContractUpdateRequest;
import com.pqb.motor_rental.entities.Motorbike;
import com.pqb.motor_rental.entities.Notification;
import com.pqb.motor_rental.entities.RentalContract;
import com.pqb.motor_rental.entities.User;
import com.pqb.motor_rental.repositories.NotificationRepository;
import com.pqb.motor_rental.repositories.UserRepository;
import com.pqb.motor_rental.security.CustomUserDetails;
import com.pqb.motor_rental.services.ContractService;
import com.pqb.motor_rental.services.NotificationService;
import com.pqb.motor_rental.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    public ApiContractController(
            ContractService contractService,
            NotificationRepository notificationRepository,
            NotificationService notificationService,
            UserRepository userRepository,
            SimpMessagingTemplate messagingTemplate,
            RestTemplate restTemplate,
            ObjectMapper objectMapper) {
        this.contractService = contractService;
        this.notificationRepository = notificationRepository;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
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
            notification.setMessage("Hợp đồng cho xe của bạn (" + contract.getBike().getName() + ") đã được duyệt! " +
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

    public ResponseEntity<String> approveContract(@PathVariable Long id, Authentication authentication) {

        // Lấy thông tin admin từ token
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User admin = userDetails.getUser();

        RentalContract approved = contractService.approveContract(id, admin);
        return ResponseEntity.ok("Contract approved successfully");
    }

//    private void sendPushNotificationsInBatch(List<Map<String, String>> notifications) {
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(notifications), headers);
//            ResponseEntity<String> response = restTemplate.postForEntity(
//                    "https://exp.host/--/api/v2/push/send",
//                    entity,
//                    String.class
//            );
//            log.info("Gửi thông báo đẩy hàng loạt thành công: {}", response.getStatusCode());
//        } catch (Exception e) {
//            log.error("Lỗi khi gửi thông báo đẩy hàng loạt: {}", e.getMessage());
//        }
//    }

}
