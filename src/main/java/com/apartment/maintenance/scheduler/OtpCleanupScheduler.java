package com.apartment.maintenance.scheduler;

import com.apartment.maintenance.repository.OtpVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OtpCleanupScheduler {

    private final OtpVerificationRepository otpVerificationRepository;

    /**
     * Runs every hour and removes expired OTPs.
     */
    @Scheduled(cron = "0 0 * * * *")
    public void cleanupExpiredOtps() {

        long deletedCount =
                otpVerificationRepository.deleteByExpiresAtBefore(
                        LocalDateTime.now()
                );

        if (deletedCount > 0) {
            log.info("OTP Cleanup completed. {} expired OTP(s) deleted.", deletedCount);
        }
    }
}