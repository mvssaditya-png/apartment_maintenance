package com.apartment.maintenance.repository;

import com.apartment.maintenance.entity.SocietyBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SocietyBalanceRepository
        extends JpaRepository<SocietyBalance, UUID> {
    @Query(value = """
        SELECT *
        FROM society_balance
        WHERE site_id = :siteId
    """, nativeQuery = true)
    SocietyBalanceProjection findBalance(UUID siteId);

    @Query(value = """
        select coalesce(opening_balance, 0)
        from society_balance
        where site_id = :siteId
        """, nativeQuery = true)
    double getOpeningBalance(UUID siteId);
}
