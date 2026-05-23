package com.apartment.maintenance.repository;
import com.apartment.maintenance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByEmail(String email);
    List<User> findBySiteId(UUID siteId);
    UUID findSiteIdByUserId(UUID userId);
    List<User> findBySiteIdAndRole(UUID siteId, String role);
    @Query(value = """
select 
u.user_id,
site.site_id,
fl.flat_id,
site.site_name,
u.flat_number,
u.email,
u.is_active,
u.name,
u.phone_number,
u.role,
u.created_at
from users u
join flats fl on fl.flat_id = u.flat_id
join sites site on site.site_id = fl.site_id
where u.user_id = :userId
""", nativeQuery = true)
    List<Object[]> getLoggedInUserDetails(UUID userId);
}