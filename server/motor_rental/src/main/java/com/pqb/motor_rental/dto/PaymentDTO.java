package com.pqb.motor_rental.dto;

public class PaymentDTO {


    public static class VNPayResponse {
        private String paymentUrl;
        private String txnRef;
        private String expireDate;

        public VNPayResponse(String paymentUrl, String txnRef, String expireDate) {
            this.paymentUrl = paymentUrl;
            this.txnRef = txnRef;
            this.expireDate = expireDate;
        }
    }


}
