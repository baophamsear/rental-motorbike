package com.pqb.motor_rental.enums;

public enum BrandServiceFee {
    HONDA("Honda", 0.20),
    VISION("Vision", 0.25),
    YAMAHA("Yamaha", 0.15);

    private final String brandName;
    private final double feeRate;
    BrandServiceFee(String brandName, double feeRate) {
        this.brandName = brandName;
        this.feeRate = feeRate;
    }
    public static double getFeeRate(String brandName) {
        for (BrandServiceFee b : values()) {
            if (b.brandName.equalsIgnoreCase(brandName)) {
                return b.feeRate;
            }
        }
        return 0.1;
    }
}