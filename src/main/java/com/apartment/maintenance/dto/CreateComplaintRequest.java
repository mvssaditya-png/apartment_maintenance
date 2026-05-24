package com.apartment.maintenance.dto;

import lombok.Data;

@Data
public class CreateComplaintRequest {
    private String title;
    private String description;
    private String category;
    private String imageUrl;
}