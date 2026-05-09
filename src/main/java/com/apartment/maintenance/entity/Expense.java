package com.apartment.maintenance.entity;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "expenses")
@Getter
@Setter
public class Expense {

    @Id
    @GeneratedValue
    private UUID expenseId;

    private UUID siteId;

    private String title;
    @Column(name = "description")
    private String description;

    private BigDecimal amount;

    private LocalDate expenseDate;
    @Column(name = "category")
    private String category;

    private String receiptUrl;

    private UUID createdBy;
    @CreationTimestamp
    @Column(name = "created_at",updatable = false)
    private LocalDateTime createdAt;
}