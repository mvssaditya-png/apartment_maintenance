package com.apartment.maintenance.repository;

import com.apartment.maintenance.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MeetingRepository extends JpaRepository<Meeting, UUID> {

    List<Meeting> findBySiteIdOrderByMeetingDateDesc(UUID siteId);

    List<Meeting> findBySiteIdAndActiveTrueOrderByMeetingDateDesc(UUID siteId);
}