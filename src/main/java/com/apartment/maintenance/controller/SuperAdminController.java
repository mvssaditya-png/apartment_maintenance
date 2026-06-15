package com.apartment.maintenance.controller;

import com.apartment.maintenance.dto.*;
import com.apartment.maintenance.service.SuperAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/super-admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    @PostMapping("/sites")
    public SiteResponse createSite(
            @RequestBody CreateSiteRequest request
    ) {
        return superAdminService.createSite(
                request
        );
    }

    /*@GetMapping("/sites")
    public List<SiteResponse> getSites() {
        return superAdminService.getAllSites();
    }*/

    @GetMapping("/dashboard")
    public SuperAdminDashboardResponse getDashboard() {

        return superAdminService.getDashboard();
    }

    @GetMapping("/subscription-plans")
    public List<SubscriptionPlanResponse> getSubscriptionPlans() {
        return superAdminService.getSubscriptionPlans();
    }

    @PostMapping("/subscription-plans")
    public SubscriptionPlanResponse createSubscriptionPlan(
            @RequestBody SubscriptionPlanRequest request
    ) {
        return superAdminService.createSubscriptionPlan(request);
    }

    @PutMapping("/subscription-plans/{planId}")
    public SubscriptionPlanResponse updateSubscriptionPlan(
            @PathVariable UUID planId,
            @RequestBody SubscriptionPlanRequest request
    ) {
        return superAdminService.updateSubscriptionPlan(planId, request);
    }

    @PatchMapping("/subscription-plans/{planId}/toggle")
    public SubscriptionPlanResponse toggleSubscriptionPlan(
            @PathVariable UUID planId
    ) {
        return superAdminService.toggleSubscriptionPlan(planId);
    }

    @GetMapping("/sites")
    public List<SiteSummaryResponse> getSites() {
        return superAdminService.getSites();
    }
}