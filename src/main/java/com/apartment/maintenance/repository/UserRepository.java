package com.apartment.maintenance.repository;
import com.apartment.maintenance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByPhoneNumber(String phoneNumber);
    UUID findSiteIdByUserId(UUID userId);
    List<User> findBySiteIdAndRole(UUID siteId, String role);
}