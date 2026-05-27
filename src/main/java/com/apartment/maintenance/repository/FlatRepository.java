package com.apartment.maintenance.repository;

import com.apartment.maintenance.entity.Flat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    @Query(value = """
        select
            fl.flat_id,
            fl.flat_number,
            min(u.name) as owner_name
        from flats fl
        left join users u
            on u.flat_id = fl.flat_id
            and u.site_id = fl.site_id
        where fl.site_id = :siteId
        and fl.is_active = true
        group by fl.flat_id, fl.flat_number
        order by fl.flat_number
        """, nativeQuery = true)
    List<Object[]> findFlatOptions(UUID siteId);
}
