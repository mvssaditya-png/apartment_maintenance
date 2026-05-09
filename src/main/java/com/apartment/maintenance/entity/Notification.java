package com.apartment.maintenance.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Data
public class Notification {

    @Id
    @GeneratedValue
    private UUID notificationId;

    private UUID userId;
    private UUID siteId;

    private String title;
    private String message;
    private String type;

    private Boolean isRead = false;

    private LocalDateTime createdAt;
}