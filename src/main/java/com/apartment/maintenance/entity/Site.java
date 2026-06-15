package com.apartment.maintenance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "sites")
public class Site {

    @Id
    @Column(name = "site_id")
    private UUID siteId;

    @Column(name = "site_name")
    private String siteName;

    @Column(name = "maintenance_amount")
    private BigDecimal maintenanceAmount;

    @Column(name = "opening_balance")
    private BigDecimal openingBalance;

    @Column(name = "total_flats")
    private Integer totalFlats;

    @Column(name = "trial_start_date")
    private LocalDate trialStartDate;

    @Column(name = "trial_end_date")
    private LocalDate trialEndDate;

    @Column(name = "subscription_status")
    private String subscriptionStatus;

    @Column(name = "subscription_start_date")
    private LocalDate subscriptionStartDate;

    @Column(name = "subscription_end_date")
    private LocalDate subscriptionEndDate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    public UUID getSiteId() {
        return siteId;
    }

    public void setSiteId(UUID siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public BigDecimal getMaintenanceAmount() {
        return maintenanceAmount;
    }

    public void setMaintenanceAmount(BigDecimal maintenanceAmount) {
        this.maintenanceAmount = maintenanceAmount;
    }

    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(BigDecimal openingBalance) {
        this.openingBalance = openingBalance;
    }

    public Integer getTotalFlats() {
        return totalFlats;
    }

    public void setTotalFlats(Integer totalFlats) {
        this.totalFlats = totalFlats;
    }

    public LocalDate getTrialStartDate() {
        return trialStartDate;
    }

    public void setTrialStartDate(LocalDate trialStartDate) {
        this.trialStartDate = trialStartDate;
    }

    public LocalDate getTrialEndDate() {
        return trialEndDate;
    }

    public void setTrialEndDate(LocalDate trialEndDate) {
        this.trialEndDate = trialEndDate;
    }

    public String getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(String subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    public LocalDate getSubscriptionStartDate() {
        return subscriptionStartDate;
    }

    public void setSubscriptionStartDate(LocalDate subscriptionStartDate) {
        this.subscriptionStartDate = subscriptionStartDate;
    }

    public LocalDate getSubscriptionEndDate() {
        return subscriptionEndDate;
    }

    public void setSubscriptionEndDate(LocalDate subscriptionEndDate) {
        this.subscriptionEndDate = subscriptionEndDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}