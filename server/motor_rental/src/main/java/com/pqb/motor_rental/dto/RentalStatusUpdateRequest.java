package com.pqb.motor_rental.dto;

import com.pqb.motor_rental.enums.RentalStatus;

public class RentalStatusUpdateRequest {
    private RentalStatus status = RentalStatus.pending; // 👈 mặc định là 'pending'

    public RentalStatus getStatus() {
        return status;
    }

    public void setStatus(RentalStatus status) {
        this.status = status != null ? status : RentalStatus.pending;
    }
}

