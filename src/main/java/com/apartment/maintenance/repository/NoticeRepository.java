package com.apartment.maintenance.repository;

import com.apartment.maintenance.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NoticeRepository extends JpaRepository<Notice, UUID> {

    List<Notice> findBySiteIdAndActiveTrueOrderByCreatedAtDesc(UUID siteId);

    List<Notice> findBySiteIdOrderByCreatedAtDesc(UUID siteId);
}