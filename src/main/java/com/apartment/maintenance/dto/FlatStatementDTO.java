package com.apartment.maintenance.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlatStatementDTO {

    private LocalDateTime date;
    private String description;
    private BigDecimal debit;
    private BigDecimal credit;
    private BigDecimal balanceAfter;
    private String receiptPdfUrl;
}
