package com.apartment.maintenance.repository;

import com.apartment.maintenance.entity.SocietyLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SocietyLedgerRepository
        extends JpaRepository<SocietyLedger, UUID> {

    Optional<SocietyLedger>
    findTopBySiteIdOrderByCreatedAtDesc(UUID siteId);

    @Query(value = """
    SELECT balance_after
    FROM society_ledger
    WHERE site_id = :siteId
    ORDER BY created_at DESC
    LIMIT 1
""", nativeQuery = true)
    BigDecimal getCurrentBalance(UUID siteId);
}
