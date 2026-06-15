package com.apartment.maintenance.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "subscription_plans")
public class SubscriptionPlan {

    @Id
    @Column(name = "plan_id")
    private UUID planId;

    @Column(name = "min_flats")
    private Integer minFlats;

    @Column(name = "max_flats")
    private Integer maxFlats;

    @Column(name = "duration_months")
    private Integer durationMonths;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public UUID getPlanId() {
        return planId;
    }

    public void setPlanId(UUID planId) {
        this.planId = planId;
    }

    public Integer getMinFlats() {
        return minFlats;
    }

    public void setMinFlats(Integer minFlats) {
        this.minFlats = minFlats;
    }

    public Integer getMaxFlats() {
        return maxFlats;
    }

    public void setMaxFlats(Integer maxFlats) {
        this.maxFlats = maxFlats;
    }

    public Integer getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(Integer durationMonths) {
        this.durationMonths = durationMonths;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}