package com.apartment.maintenance.repository;

import java.math.BigDecimal;
import java.util.UUID;

public interface SocietyBalanceProjection {

    UUID getSiteId();

    BigDecimal getOpeningBalance();

    BigDecimal getTotalCollected();

    BigDecimal getTotalExpenses();

    BigDecimal getCurrentBalance();
}