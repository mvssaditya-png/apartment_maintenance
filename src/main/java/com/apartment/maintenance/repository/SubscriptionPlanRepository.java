package com.apartment.maintenance.repository;

import com.apartment.maintenance.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface SubscriptionPlanRepository
        extends JpaRepository<SubscriptionPlan, UUID> {

    @Query("""
        select p
        from SubscriptionPlan p
        where p.active = true
        and p.minFlats <= :flatCount
        and (p.maxFlats is null or p.maxFlats >= :flatCount)
        order by p.durationMonths asc
    """)
    List<SubscriptionPlan> findPlansForFlatCount(Integer flatCount);

    List<SubscriptionPlan> findAllByOrderByMinFlatsAscDurationMonthsAsc();
}