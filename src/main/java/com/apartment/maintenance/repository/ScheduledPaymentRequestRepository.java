package com.apartment.maintenance.repository;

import com.apartment.maintenance.entity.ScheduledPaymentRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ScheduledPaymentRequestRepository
        extends JpaRepository<ScheduledPaymentRequest, UUID> {

    List<ScheduledPaymentRequest> findBySiteIdOrderByCreatedAtDesc(UUID siteId);

    List<ScheduledPaymentRequest> findByActiveTrue();
}