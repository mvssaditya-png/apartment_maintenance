package com.apartment.maintenance.service;

import com.apartment.maintenance.dto.*;
import com.apartment.maintenance.entity.Site;
import com.apartment.maintenance.entity.SubscriptionPlan;
import com.apartment.maintenance.entity.User;
import com.apartment.maintenance.repository.SiteRepository;
import com.apartment.maintenance.repository.SubscriptionPlanRepository;
import com.apartment.maintenance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SuperAdminService {

    private final SiteRepository siteRepository;
    private final UserRepository userRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SmsEventService smsEventService;
    @Transactional
    public SiteResponse createSite(
            CreateSiteRequest request
    ) {

        if (userRepository.existsByPhoneNumber(
                request.getAdminPhoneNumber()
        )) {
            throw new RuntimeException(
                    "Phone number already exists"
            );
        }

        Site site = new Site();

        site.setSiteId(UUID.randomUUID());
        site.setSiteName(request.getSiteName());
        site.setMaintenanceAmount(
                request.getMaintenanceAmount()
        );
        site.setOpeningBalance(
                request.getOpeningBalance()
        );

        site.setTotalFlats(
                request.getTotalFlats()
        );

        site.setTrialStartDate(
                LocalDate.now()
        );

        site.setTrialEndDate(
                LocalDate.now().plusMonths(3)
        );

        site.setSubscriptionStatus("TRIAL");

        site.setIsActive(true);
        site.setAddress(request.getAddress());
        site.setCity(request.getCity());
        site.setState(request.getState());
        siteRepository.save(site);
        User admin = new User();

        admin.setSiteId(site.getSiteId());

        admin.setName(request.getAdminName());

        admin.setPhoneNumber(
                request.getAdminPhoneNumber()
        );

        admin.setEmail(
                request.getAdminEmail()
        );

        admin.setRole("ADMIN");

        admin.setIsActive(true);

        userRepository.save(admin);
        smsEventService.trialStarted(admin, site);

        return mapToResponse(site);
    }

    public List<SiteResponse> getAllSites() {

        return siteRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private SiteResponse mapToResponse(
            Site site
    ) {

        return SiteResponse.builder()
                .siteId(site.getSiteId())
                .siteName(site.getSiteName())
                .totalFlats(site.getTotalFlats())
                .subscriptionStatus(
                        site.getSubscriptionStatus()
                )
                .trialStartDate(
                        site.getTrialStartDate()
                )
                .trialEndDate(
                        site.getTrialEndDate()
                )
                .isActive(
                        site.getIsActive()
                )
                .build();
    }

    public SuperAdminDashboardResponse getDashboard() {

        return SuperAdminDashboardResponse.builder()
                .totalSites(
                        siteRepository.countAllSites()
                )
                .trialSites(
                        siteRepository.countTrialSites()
                )
                .activeSites(
                        siteRepository.countActiveSites()
                )
                .expiredSites(
                        siteRepository.countExpiredSites()
                )
                .inactiveSites(
                        siteRepository.countInactiveSites()
                )
                .build();
    }
    public List<SubscriptionPlanResponse> getSubscriptionPlans() {

        return subscriptionPlanRepository
                .findAllByOrderByMinFlatsAscDurationMonthsAsc()
                .stream()
                .map(this::mapPlanToResponse)
                .toList();
    }

    @Transactional
    public SubscriptionPlanResponse createSubscriptionPlan(
            SubscriptionPlanRequest request
    ) {
        validatePlanRequest(request);

        SubscriptionPlan plan = new SubscriptionPlan();

        plan.setPlanId(UUID.randomUUID());
        plan.setMinFlats(request.getMinFlats());
        plan.setMaxFlats(request.getMaxFlats());
        plan.setDurationMonths(request.getDurationMonths());
        plan.setAmount(request.getAmount());
        plan.setActive(request.getActive() == null || request.getActive());

        SubscriptionPlan savedPlan =
                subscriptionPlanRepository.save(plan);

        return mapPlanToResponse(savedPlan);
    }

    @Transactional
    public SubscriptionPlanResponse updateSubscriptionPlan(
            UUID planId,
            SubscriptionPlanRequest request
    ) {
        validatePlanRequest(request);

        SubscriptionPlan plan =
                subscriptionPlanRepository.findById(planId)
                        .orElseThrow(() -> new RuntimeException("Plan not found"));

        plan.setMinFlats(request.getMinFlats());
        plan.setMaxFlats(request.getMaxFlats());
        plan.setDurationMonths(request.getDurationMonths());
        plan.setAmount(request.getAmount());

        if (request.getActive() != null) {
            plan.setActive(request.getActive());
        }

        SubscriptionPlan savedPlan =
                subscriptionPlanRepository.save(plan);

        return mapPlanToResponse(savedPlan);
    }

    @Transactional
    public SubscriptionPlanResponse toggleSubscriptionPlan(
            UUID planId
    ) {
        SubscriptionPlan plan =
                subscriptionPlanRepository.findById(planId)
                        .orElseThrow(() -> new RuntimeException("Plan not found"));

        plan.setActive(!Boolean.TRUE.equals(plan.getActive()));

        SubscriptionPlan savedPlan =
                subscriptionPlanRepository.save(plan);

        return mapPlanToResponse(savedPlan);
    }

    private void validatePlanRequest(
            SubscriptionPlanRequest request
    ) {
        if (request.getMinFlats() == null || request.getMinFlats() <= 0) {
            throw new RuntimeException("Minimum flats is required");
        }

        if (request.getMaxFlats() != null
                && request.getMaxFlats() < request.getMinFlats()) {
            throw new RuntimeException("Maximum flats cannot be less than minimum flats");
        }

        if (request.getDurationMonths() == null
                || request.getDurationMonths() <= 0) {
            throw new RuntimeException("Duration months is required");
        }

        if (request.getAmount() == null
                || request.getAmount().doubleValue() <= 0) {
            throw new RuntimeException("Amount is required");
        }
    }

    private SubscriptionPlanResponse mapPlanToResponse(
            SubscriptionPlan plan
    ) {
        String range =
                plan.getMaxFlats() == null
                        ? plan.getMinFlats() + "+ flats"
                        : plan.getMinFlats() + " - " + plan.getMaxFlats() + " flats";

        return SubscriptionPlanResponse.builder()
                .planId(plan.getPlanId())
                .minFlats(plan.getMinFlats())
                .maxFlats(plan.getMaxFlats())
                .durationMonths(plan.getDurationMonths())
                .amount(plan.getAmount())
                .active(plan.getActive())
                .label(
                        range
                                + " | "
                                + plan.getDurationMonths()
                                + " Months - ₹"
                                + plan.getAmount()
                )
                .build();
    }

    public List<SiteSummaryResponse> getSites() {

        return siteRepository.getAllSitesSummary()
                .stream()
                .map(row ->
                        SiteSummaryResponse.builder()
                                .siteId((UUID) row[0])
                                .siteName((String) row[1])
                                .adminName((String) row[2])
                                .adminPhone((String) row[3])
                                .totalFlats(
                                        row[4] == null
                                                ? 0
                                                : ((Number) row[4]).intValue()
                                )
                                .subscriptionStatus((String) row[5])
                                .trialEndDate(
                                        row[6] == null
                                                ? null
                                                : ((LocalDate) row[6])
                                )
                                .subscriptionEndDate(
                                        row[7] == null
                                                ? null
                                                : ((LocalDate) row[7])
                                )
                                .active((Boolean) row[8])
                                .openingBalance((BigDecimal) row[9])
                                .maintenanceAmount((BigDecimal) row[10])
                                .address((String) row[11])
                                .city((String) row[12])
                                .state((String) row[13])
                                .build()
                )
                .toList();
    }

    @Transactional
    public SiteSummaryResponse updateSite(
            UUID siteId,
            UpdateSiteRequest request
    ) {
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new RuntimeException("Site not found"));

        site.setSiteName(request.getSiteName());
        site.setAddress(request.getAddress());
        site.setCity(request.getCity());
        site.setState(request.getState());
        site.setMaintenanceAmount(request.getMaintenanceAmount());
        site.setOpeningBalance(request.getOpeningBalance());
        site.setTotalFlats(request.getTotalFlats());

        siteRepository.save(site);

        List<User> admins =
                userRepository.findBySiteIdAndRole(siteId, "ADMIN");

        User admin;

        if (admins.isEmpty()) {
            admin = new User();
            admin.setSiteId(siteId);
            admin.setRole("ADMIN");
            admin.setIsActive(true);
        } else {
            admin = admins.get(0);
        }

        admin.setName(request.getAdminName());
        admin.setPhoneNumber(request.getAdminPhoneNumber());
        admin.setEmail(request.getAdminEmail());

        userRepository.save(admin);

        return getSiteSummaryById(siteId);
    }

    @Transactional
    public SiteSummaryResponse toggleSite(
            UUID siteId
    ) {
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new RuntimeException("Site not found"));

        site.setIsActive(!Boolean.TRUE.equals(site.getIsActive()));

        siteRepository.save(site);

        return getSiteSummaryById(siteId);
    }

    private SiteSummaryResponse getSiteSummaryById(UUID siteId) {
        return getSites()
                .stream()
                .filter(site -> site.getSiteId().equals(siteId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Site not found"));
    }
}