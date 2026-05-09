package com.apartment.maintenance.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreatePaymentRequest {

    private String title;
    private String description;
    private Double amount;
    private String requestType;

    private Integer paymentMonth;
    private Integer paymentYear;

    private LocalDate dueDate;

}