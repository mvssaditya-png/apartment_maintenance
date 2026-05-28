package com.apartment.maintenance.dto;

public interface DefaulterResponse {
    String getFlatNumber();
    String getOwnerName();

    Long getMaintenancePendingMonths();
    Double getMaintenanceDue();

    Long getSpecialRequestCount();
    Double getSpecialRequestDue();

    Long getOtherPendingCount();
    Double getOtherDue();

    Double getTotalDue();
}