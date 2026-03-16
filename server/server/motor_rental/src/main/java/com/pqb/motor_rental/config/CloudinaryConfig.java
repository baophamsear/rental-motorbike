package com.pqb.motor_rental.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "pqbou11",
                "api_key", "618391474144729",
                "api_secret", "T1P03c8NmRLkJkhqT9Ggj6b2cx0",
                "secure", true
        ));
    }
}
