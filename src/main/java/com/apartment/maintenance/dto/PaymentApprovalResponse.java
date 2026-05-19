package com.apartment.maintenance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentApprovalResponse {

    private UUID paymentId;
    private UUID flatId;

    private String flatNumber;

    private BigDecimal amount;

    private Integer paymentMonth;
    private Integer paymentYear;

    private String paymentStatus;
    private String paymentMode;
    private String requestType;

    private String receiptUrl;

    private LocalDateTime createdAt;
}