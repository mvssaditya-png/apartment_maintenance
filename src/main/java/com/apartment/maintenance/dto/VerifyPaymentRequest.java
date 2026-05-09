package com.apartment.maintenance.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class VerifyPaymentRequest {

    private UUID paymentId;
    private boolean approved;
}