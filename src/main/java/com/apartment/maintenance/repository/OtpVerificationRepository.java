package com.apartment.maintenance.repository;

import com.apartment.maintenance.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, UUID> {

    Optional<OtpVerification> findTopByPhoneNumberOrderByCreatedAtDesc(String phoneNumber);

    @Transactional
    long deleteByExpiresAtBefore(LocalDateTime dateTime);

    @Transactional
    @Modifying
    @Query("""
    update OtpVerification o
       set o.verified = true
     where o.phoneNumber = :phoneNumber
       and o.verified = false
""")
    void invalidatePreviousOtps(String phoneNumber);

    @Query("""
    select count(o)
      from OtpVerification o
     where o.phoneNumber = :phoneNumber
       and o.createdAt >= :fromTime
""")
    long countOtpRequestsSince(
            String phoneNumber,
            LocalDateTime fromTime
    );

}