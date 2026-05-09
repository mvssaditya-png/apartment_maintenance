package com.apartment.maintenance.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResidentDashboardDTO {

    private String flatNumber;
    private String ownerName;

    private BigDecimal pendingAmount;
    private BigDecimal totalPaid;

    private Long pendingMonths;
    private LocalDateTime lastPaymentDate;

    private BigDecimal societyBalance;
}
