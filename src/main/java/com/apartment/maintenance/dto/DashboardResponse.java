package com.apartment.maintenance.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DashboardResponse {

    private BigDecimal currentBalance;
    private BigDecimal totalCollected;
    private BigDecimal totalExpenses;

    private long totalFlats;
    private long paidFlats;
    private long pendingFlats;

    private BigDecimal myPendingAmount;
    private String myPaymentStatus;
    private BigDecimal specialRequestCollected;
}