package com.apartment.maintenance.repository;

import com.apartment.maintenance.entity.EmergencyContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EmergencyContactRepository
        extends JpaRepository<EmergencyContact, UUID> {

    List<EmergencyContact> findBySiteIdAndActiveTrueOrderByPriorityAscCreatedAtDesc(
            UUID siteId
    );

    List<EmergencyContact> findBySiteIdOrderByPriorityAscCreatedAtDesc(
            UUID siteId
    );
}