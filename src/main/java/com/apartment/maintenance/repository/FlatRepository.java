package com.apartment.maintenance.repository;

import com.apartment.maintenance.entity.Flat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FlatRepository extends JpaRepository<Flat, UUID> {

    List<Flat> findBySiteId(UUID siteId);

    Optional<Flat> findByFlatNumberAndSiteId(
            String flatNumber,
            UUID siteId
    );

    long countBySiteId(UUID siteId);

    List<Flat> findBySiteIdAndIsActiveTrue(UUID siteId);
    Optional<Flat> findBySiteIdAndFlatNumber(
            UUID siteId,
            String flatNumber
    );

    long countBySiteIdAndIsActiveTrue(UUID siteId);
}
