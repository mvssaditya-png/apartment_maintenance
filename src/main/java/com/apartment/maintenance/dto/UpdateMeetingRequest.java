package com.apartment.maintenance.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UpdateMeetingRequest {
    private String title;
    private String description;
    private LocalDateTime meetingDate;
    private String location;
    private Boolean active;
    private String status;
}