package com.apartment.maintenance.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateMeetingRequest {
    private String title;
    private String description;
    private LocalDateTime meetingDate;
    private String location;
}