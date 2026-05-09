package com.apartment.maintenance.dto;

public interface DefaulterResponse {

    String getFlatNumber();

    String getOwnerName();

    Long getPendingMonths();

    Double getTotalDue();
}
