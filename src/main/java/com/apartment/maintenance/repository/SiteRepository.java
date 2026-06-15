package com.apartment.maintenance.repository;

import com.apartment.maintenance.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SiteRepository
        extends JpaRepository<Site, UUID> {
    @Query(value = """
        select
            s.site_id,
            s.site_name,
            s.address,
            count(f.flat_id) as total_flats
        from sites s
        left join flats f
            on f.site_id = s.site_id
            and f.is_active = true
        where s.site_id = :siteId
        group by s.site_id, s.site_name, s.address
        """, nativeQuery = true)
    List<Object[]> getSiteProfile(UUID siteId);

    @Query("""
    select count(s)
    from Site s
""")
    Long countAllSites();

    @Query("""
    select count(s)
    from Site s
    where s.subscriptionStatus = 'TRIAL'
""")
    Long countTrialSites();

    @Query("""
    select count(s)
    from Site s
    where s.subscriptionStatus = 'ACTIVE'
""")
    Long countActiveSites();

    @Query("""
    select count(s)
    from Site s
    where s.subscriptionStatus = 'EXPIRED'
""")
    Long countExpiredSites();

    @Query("""
    select count(s)
    from Site s
    where s.isActive = false
""")
    Long countInactiveSites();
}
