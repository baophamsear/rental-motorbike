package com.pqb.motor_rental.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class MoMoConfig {
    public static final String ENDPOINT = "https://test-payment.momo.vn/v2/gateway/api/create";
    public static final String PARTNER_CODE = "MOMO";
    public static final String ACCESS_KEY = "F8BBA842ECF85";
    public static final String SECRET_KEY = "K951B6PE1waDMi640xX08PD3vg6EkVlz";
    public static final String REDIRECT_URL = "https://momo.vn";
    public static final String IPN_URL = "https://motorbike-rental-23.onrender.com/api/momo/callback";
}