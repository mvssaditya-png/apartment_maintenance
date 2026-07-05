package com.apartment.maintenance.scheduler;

import com.apartment.maintenance.entity.Site;
import com.apartment.maintenance.entity.User;
import com.apartment.maintenance.repository.SiteRepository;
import com.apartment.maintenance.repository.UserRepository;
import com.apartment.maintenance.service.SmsEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionStatusScheduler {

    private final SiteRepository siteRepository;
    private final UserRepository userRepository;
    private final SmsEventService smsEventService;

    @Scheduled(cron = "0 30 9 * * *")
    public void checkTrialAndSubscriptionStatus() {

        LocalDate today = LocalDate.now();

        List<Site> sites = siteRepository.findAll();

        for (Site site : sites) {

            List<User> admins =
                    userRepository.findBySiteIdAndRoleAndIsActive(
                            site.getSiteId(),
                            "ADMIN",
                            true
                    );

            if (admins.isEmpty()) {
                continue;
            }

            if ("TRIAL".equalsIgnoreCase(site.getSubscriptionStatus())
                    && site.getTrialEndDate() != null) {

                long daysLeft =
                        ChronoUnit.DAYS.between(today, site.getTrialEndDate());

                if (daysLeft == 7 || daysLeft == 3 || daysLeft == 1) {
                    for (User admin : admins) {
                        smsEventService.trialExpiryReminder(admin, site, daysLeft);
                    }
                }

                if (daysLeft < 0) {
                    site.setSubscriptionStatus("EXPIRED");
                    siteRepository.save(site);

                    for (User admin : admins) {
                        smsEventService.trialExpired(admin, site);
                    }
                }
            }

            if ("ACTIVE".equalsIgnoreCase(site.getSubscriptionStatus())
                    && site.getSubscriptionEndDate() != null) {

                long daysLeft =
                        ChronoUnit.DAYS.between(today, site.getSubscriptionEndDate());

                if (daysLeft == 7 || daysLeft == 3 || daysLeft == 1) {
                    for (User admin : admins) {
                        smsEventService.subscriptionExpiryReminder(admin, site, daysLeft);
                    }
                }

                if (daysLeft < 0) {
                    site.setSubscriptionStatus("EXPIRED");
                    siteRepository.save(site);

                    for (User admin : admins) {
                        smsEventService.subscriptionExpired(admin, site);
                    }
                }
            }
        }
    }
}