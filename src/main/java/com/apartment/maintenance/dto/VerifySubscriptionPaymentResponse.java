package com.apartment.maintenance.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class VerifySubscriptionPaymentResponse {

    private String status;

    private String message;

    private LocalDate subscriptionStartDate;

    private LocalDate subscriptionEndDate;
}