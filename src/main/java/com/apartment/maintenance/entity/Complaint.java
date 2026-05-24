package com.apartment.maintenance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "complaints")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "complaint_id")
    private UUID complaintId;

    @Column(name = "site_id", nullable = false)
    private UUID siteId;

    @Column(name = "flat_id")
    private UUID flatId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "category")
    private String category;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "admin_response")
    private String adminResponse;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}