package com.apartment.maintenance.dto;

import lombok.Data;

@Data
public class UpdateComplaintStatusRequest {
    private String status;
    private String adminResponse;
}