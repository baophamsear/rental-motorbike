package com.pqb.motor_rental.services.impl;

import com.pqb.motor_rental.config.VNPayConfig;
import com.pqb.motor_rental.dto.PaymentDTO;
import com.pqb.motor_rental.services.VNPayService;
import com.pqb.motor_rental.util.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class VNPayServiceImpl implements VNPayService {

    private final VNPayConfig vnPayConfig;

    public VNPayServiceImpl(VNPayConfig vnPayConfig) {
        this.vnPayConfig = vnPayConfig;
    }

    @Override
    public PaymentDTO.VNPayResponse createVnPayPayment(HttpServletRequest request, long amount, String orderInfo, String bankCode) {
        // 1. Sinh txnRef
        String txnRef = "INV" + System.currentTimeMillis();

        // 2. Tạo params từ config
        Map<String, String> params = vnPayConfig.getVNPayConfig(orderInfo, txnRef);
        params.put("vnp_Amount", String.valueOf(amount * 100));
        params.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));

        // ✅ Optional: Chỉ thêm nếu được phép hoặc cần test
        if (bankCode != null && !bankCode.isBlank()) {
            params.put("vnp_BankCode", bankCode);
        }

        // 3. Sinh chữ ký và tạo query URL
        String dataToSign = VNPayUtil.getPaymentURL(params, false); // Không encode key
        String secureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), dataToSign);

        String queryUrl = VNPayUtil.getPaymentURL(params, true); // Có encode key
        queryUrl += "&vnp_SecureHash=" + secureHash;

        System.out.println("🧾 [VNPay] Dữ liệu ký (dataToSign):");
        System.out.println(dataToSign);

        System.out.println("🔑 SecretKey: " + vnPayConfig.getSecretKey());
        System.out.println("🔐 SecureHash (vnp_SecureHash):");
        System.out.println(secureHash);




        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;

        System.out.println("🌐 Final paymentUrl:");
        System.out.println(paymentUrl);

        // 4. Trả về DTO
        return new PaymentDTO.VNPayResponse(
                paymentUrl,
                txnRef,
                params.get("vnp_ExpireDate")
        );
    }
}
