package com.apartment.maintenance.service;

import com.apartment.maintenance.dto.CreatePaymentRequest;
import com.apartment.maintenance.dto.CreateScheduledPaymentRequest;
import com.apartment.maintenance.dto.UpdateScheduledPaymentRequest;
import com.apartment.maintenance.entity.ScheduledPaymentRequest;
import com.apartment.maintenance.entity.User;
import com.apartment.maintenance.exception.UnauthorizedActionException;
import com.apartment.maintenance.repository.PaymentRequestRepository;
import com.apartment.maintenance.repository.ScheduledPaymentRequestRepository;
import com.apartment.maintenance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduledPaymentRequestService {

    private final ScheduledPaymentRequestRepository scheduleRepo;
    private final UserRepository userRepo;
    private final PaymentRequestService paymentRequestService;
    private final PaymentRequestRepository paymentRequestRepo;

    public List<ScheduledPaymentRequest> getSchedules(UUID userId) {
        User user = userRepo.findById(userId).orElseThrow();
        validateAdmin(user);

        return scheduleRepo.findBySiteIdOrderByCreatedAtDesc(user.getSiteId());
    }

    public ScheduledPaymentRequest createSchedule(
            UUID userId,
            CreateScheduledPaymentRequest dto
    ) {
        User user = userRepo.findById(userId).orElseThrow();
        validateAdmin(user);

        validateDueDay(dto.getDueDay());

        ScheduledPaymentRequest schedule = ScheduledPaymentRequest.builder()
                .siteId(user.getSiteId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .amount(dto.getAmount())
                .dueDay(dto.getDueDay())
                .reminderFrequencyDays(
                        dto.getReminderFrequencyDays() != null
                                ? dto.getReminderFrequencyDays()
                                : 3
                )
                .active(dto.getActive() != null ? dto.getActive() : true)
                .createdBy(userId)
                .createdAt(LocalDateTime.now())
                .build();

        return scheduleRepo.save(schedule);
    }

    public ScheduledPaymentRequest updateSchedule(
            UUID userId,
            UUID scheduleId,
            UpdateScheduledPaymentRequest dto
    ) {
        User user = userRepo.findById(userId).orElseThrow();
        validateAdmin(user);

        ScheduledPaymentRequest schedule =
                scheduleRepo.findById(scheduleId).orElseThrow();

        if (!schedule.getSiteId().equals(user.getSiteId())) {
            throw new UnauthorizedActionException("Invalid schedule");
        }

        validateDueDay(dto.getDueDay());

        schedule.setTitle(dto.getTitle());
        schedule.setDescription(dto.getDescription());
        schedule.setAmount(dto.getAmount());
        schedule.setDueDay(dto.getDueDay());
        schedule.setReminderFrequencyDays(dto.getReminderFrequencyDays());
        schedule.setActive(dto.getActive());

        return scheduleRepo.save(schedule);
    }

    public ScheduledPaymentRequest toggleSchedule(UUID userId, UUID scheduleId) {
        User user = userRepo.findById(userId).orElseThrow();
        validateAdmin(user);

        ScheduledPaymentRequest schedule =
                scheduleRepo.findById(scheduleId).orElseThrow();

        if (!schedule.getSiteId().equals(user.getSiteId())) {
            throw new UnauthorizedActionException("Invalid schedule");
        }

        schedule.setActive(!Boolean.TRUE.equals(schedule.getActive()));

        return scheduleRepo.save(schedule);
    }

    @Transactional
    public void runMonthlyScheduler() {
        LocalDate today = LocalDate.now();

        List<ScheduledPaymentRequest> schedules =
                scheduleRepo.findByActiveTrue();

        for (ScheduledPaymentRequest schedule : schedules) {

            boolean alreadyGeneratedBySchedule =
                    schedule.getLastGeneratedMonth() != null
                            && schedule.getLastGeneratedYear() != null
                            && schedule.getLastGeneratedMonth().equals(today.getMonthValue())
                            && schedule.getLastGeneratedYear().equals(today.getYear());

            if (alreadyGeneratedBySchedule) {
                continue;
            }
            boolean alreadyExists =
                    paymentRequestRepo
                            .existsBySiteIdAndPaymentMonthAndPaymentYearAndTitleIgnoreCase(
                                    schedule.getSiteId(),
                                    today.getMonthValue(),
                                    today.getYear(),
                                    schedule.getTitle()
                            );

            if (alreadyExists) {

                schedule.setLastGeneratedMonth(today.getMonthValue());
                schedule.setLastGeneratedYear(today.getYear());

                scheduleRepo.save(schedule);

                continue;
            }

            CreatePaymentRequest request = new CreatePaymentRequest();
            request.setTitle(schedule.getTitle());
            request.setDescription(schedule.getDescription());
            request.setAmount(schedule.getAmount().doubleValue());
            request.setPaymentMonth(today.getMonthValue());
            request.setPaymentYear(today.getYear());
            request.setRequestType("Maintenance");

            int dueDay = Math.min(
                    schedule.getDueDay(),
                    today.lengthOfMonth()
            );

            request.setDueDate(
                    LocalDate.of(today.getYear(), today.getMonthValue(), dueDay)
            );

            paymentRequestService.createRequest(
                    schedule.getCreatedBy(),
                    request
            );

            schedule.setLastGeneratedMonth(today.getMonthValue());
            schedule.setLastGeneratedYear(today.getYear());

            scheduleRepo.save(schedule);
        }
    }

    private void validateAdmin(User user) {
        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new UnauthorizedActionException(
                    "Only Admin can manage scheduled payment requests"
            );
        }
    }

    private void validateDueDay(Integer dueDay) {
        if (dueDay == null || dueDay < 1 || dueDay > 31) {
            throw new RuntimeException("Due day must be between 1 and 31");
        }
    }
}