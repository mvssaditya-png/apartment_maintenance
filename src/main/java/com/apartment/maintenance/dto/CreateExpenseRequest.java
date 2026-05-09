package com.apartment.maintenance.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateExpenseRequest {

    private String title;
    private String description;
    private BigDecimal amount;
    private LocalDate expenseDate;
    private String category;
    private String receiptUrl;
}