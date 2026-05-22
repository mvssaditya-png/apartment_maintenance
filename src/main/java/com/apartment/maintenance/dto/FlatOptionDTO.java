package com.apartment.maintenance.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlatOptionDTO {
    private UUID flatId;
    private String flatNumber;
    private String ownerName;
}
