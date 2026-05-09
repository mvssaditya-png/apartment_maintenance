package com.apartment.maintenance.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "sites")
public class Site {

    @Id
    private UUID siteId;

    private String siteName;

    private BigDecimal maintenanceAmount;

    private BigDecimal openingBalance;   // ✅ ADD

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
}