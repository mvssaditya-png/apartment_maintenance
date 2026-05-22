package com.apartment.maintenance.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SiteProfileDTO {
    private UUID siteId;
    private String siteName;
    private String address;
    private Long totalFlats;
}
