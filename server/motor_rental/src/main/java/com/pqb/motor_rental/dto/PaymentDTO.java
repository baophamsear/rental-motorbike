package com.pqb.motor_rental.dto;

public class PaymentDTO {

    public static class VNPayResponse {
        private String paymentUrl;
        private String txnRef;
        private String expireDate;

        public VNPayResponse() {}

        public VNPayResponse(String paymentUrl, String txnRef, String expireDate) {
            this.paymentUrl = paymentUrl;
            this.txnRef = txnRef;
            this.expireDate = expireDate;
        }

        public String getPaymentUrl() {
            return paymentUrl;
        }

        public void setPaymentUrl(String paymentUrl) {
            this.paymentUrl = paymentUrl;
        }

        public String getTxnRef() {
            return txnRef;
        }

        public void setTxnRef(String txnRef) {
            this.txnRef = txnRef;
        }

        public String getExpireDate() {
            return expireDate;
        }

        public void setExpireDate(String expireDate) {
            this.expireDate = expireDate;
        }
    }
}
