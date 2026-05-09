package com.apartment.maintenance.service;

import com.apartment.maintenance.entity.Site;
import com.apartment.maintenance.entity.User;
import com.apartment.maintenance.exception.UnauthorizedActionException;
import com.apartment.maintenance.repository.SiteRepository;
import com.apartment.maintenance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private final SiteRepository siteRepo;
    private final UserRepository userRepo;
    private final LedgerService ledgerService;
    // -------------------------
    // GET OPENING BALANCE
    // -------------------------
    public BigDecimal getOpeningBalance(UUID userId) {

        UUID siteId = userRepo.findById(userId)
                .orElseThrow()
                .getSiteId();

        Site site = siteRepo.findById(siteId)
                .orElseThrow();

        return site.getOpeningBalance();
    }

    // -------------------------
    // UPDATE OPENING BALANCE
    // -------------------------
    @Transactional
    public BigDecimal updateOpeningBalance(
            UUID userId,
            BigDecimal amount) {

        User user = userRepo.findById(userId)
                .orElseThrow();

        // ✅ ADMIN ONLY
        if (!user.getRole().equalsIgnoreCase("ADMIN")) {
            throw new UnauthorizedActionException(
                    "Only admin can update opening balance");
        }

        Site site = siteRepo.findById(user.getSiteId())
                .orElseThrow();

        site.setOpeningBalance(amount);

        siteRepo.save(site);
        ledgerService.addCredit(
                user.getSiteId(),
                amount,
                "OPENING",
                null,
                "Opening Balance"
        );
        return amount;
    }
}
