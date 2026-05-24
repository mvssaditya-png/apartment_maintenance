package com.apartment.maintenance.dto;


import lombok.Data;

@Data
public class CreateNoticeRequest {
    private String title;
    private String message;
}
