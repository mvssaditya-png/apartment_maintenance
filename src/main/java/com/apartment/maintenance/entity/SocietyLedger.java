package com.apartment.maintenance.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "society_ledger")
@Data
public class SocietyLedger {

    @Id
    @GeneratedValue
    private UUID ledgerId;

    private UUID siteId;

    private String transactionType; // CREDIT / DEBIT
    private String source;

    private UUID referenceId;

    private String description;

    private BigDecimal credit;
    private BigDecimal debit;

    private BigDecimal balanceAfter;
    @CreationTimestamp
    @Column(name = "created_at",updatable = false)
    private LocalDateTime createdAt;
}
