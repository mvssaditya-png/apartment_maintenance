package com.apartment.maintenance.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateScheduledPaymentRequest {
    private String title;
    private String description;
    private BigDecimal amount;
    private Integer dueDay;
    private Integer reminderFrequencyDays;
    private Boolean active;
}