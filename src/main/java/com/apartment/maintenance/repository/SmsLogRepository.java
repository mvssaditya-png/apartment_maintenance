package com.apartment.maintenance.repository;

import com.apartment.maintenance.entity.SmsLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SmsLogRepository extends JpaRepository<SmsLog, UUID> {
}
