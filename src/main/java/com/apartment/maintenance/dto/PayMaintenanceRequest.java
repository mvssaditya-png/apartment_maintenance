package com.apartment.maintenance.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class PayMaintenanceRequest {

    private UUID paymentId;
    private String paymentMode;
    private String receiptUrl;
}