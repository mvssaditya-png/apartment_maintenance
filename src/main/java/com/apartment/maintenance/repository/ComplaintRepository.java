package com.apartment.maintenance.repository;

import com.apartment.maintenance.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ComplaintRepository extends JpaRepository<Complaint, UUID> {

    List<Complaint> findBySiteIdOrderByCreatedAtDesc(UUID siteId);

    List<Complaint> findByUserIdOrderByCreatedAtDesc(UUID userId);
}