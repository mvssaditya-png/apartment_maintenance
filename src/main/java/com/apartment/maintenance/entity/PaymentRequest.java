package com.apartment.maintenance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @Id
    @GeneratedValue
    private UUID requestId;

    private UUID siteId;

    private String title;

    private String description;

    private Double amount;
    @Column(name = "request_type", nullable = false)
    private String requestType;

    private Integer paymentMonth;

    private Integer paymentYear;
    @Column(name = "due_date")
    private LocalDate dueDate;

    private UUID createdBy;

    private LocalDateTime createdAt = LocalDateTime.now();

    private String status;
}