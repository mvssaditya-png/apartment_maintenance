package com.apartment.maintenance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "emergency_contacts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyContact {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "contact_id")
    private UUID contactId;

    @Column(name = "site_id", nullable = false)
    private UUID siteId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "priority", nullable = false)
    private Integer priority;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}