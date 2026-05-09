package com.apartment.maintenance.dto;

public record MonthlySummaryResponse(
        Double openingBalance,
        Double collections,
        Double expenses,
        Double closingBalance
) {}