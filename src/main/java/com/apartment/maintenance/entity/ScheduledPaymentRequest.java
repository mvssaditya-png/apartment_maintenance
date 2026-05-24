package com.apartment.maintenance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "scheduled_payment_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduledPaymentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "schedule_id")
    private UUID scheduleId;

    @Column(name = "site_id", nullable = false)
    private UUID siteId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "due_day", nullable = false)
    private Integer dueDay;

    @Column(name = "reminder_frequency_days", nullable = false)
    private Integer reminderFrequencyDays;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "last_generated_month")
    private Integer lastGeneratedMonth;

    @Column(name = "last_generated_year")
    private Integer lastGeneratedYear;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}