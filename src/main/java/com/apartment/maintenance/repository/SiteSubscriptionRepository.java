package com.apartment.maintenance.repository;

import com.apartment.maintenance.entity.SiteSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SiteSubscriptionRepository
        extends JpaRepository<SiteSubscription, UUID> {

    Optional<SiteSubscription> findByRazorpayOrderId(String razorpayOrderId);
}