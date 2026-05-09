package com.apartment.maintenance.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "society_balance")
@Data
public class SocietyBalance {

    @Id
    private UUID siteId;

    private String siteName;

    private BigDecimal totalCollected;

    private BigDecimal totalExpenses;

    private BigDecimal currentBalance;
}
