package com.apartment.maintenance.service;

import com.apartment.maintenance.entity.SocietyLedger;
import com.apartment.maintenance.repository.SocietyLedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LedgerService {

    private final SocietyLedgerRepository ledgerRepo;

    @Transactional
    public void addCredit(
            UUID siteId,
            BigDecimal amount,
            String source,
            UUID referenceId,
            String description) {

        BigDecimal lastBalance =
                ledgerRepo
                        .findTopBySiteIdOrderByCreatedAtDesc(siteId)
                        .map(SocietyLedger::getBalanceAfter)
                        .orElse(BigDecimal.ZERO);

        SocietyLedger entry = new SocietyLedger();

        entry.setSiteId(siteId);
        entry.setTransactionType("CREDIT");
        entry.setSource(source);
        entry.setReferenceId(referenceId);
        entry.setDescription(description);
        entry.setCredit(amount);
        entry.setDebit(BigDecimal.ZERO);
        entry.setBalanceAfter(lastBalance.add(amount));

        ledgerRepo.save(entry);
    }

    @Transactional
    public void addDebit(
            UUID siteId,
            BigDecimal amount,
            String source,
            UUID referenceId,
            String description) {

        BigDecimal lastBalance =
                ledgerRepo
                        .findTopBySiteIdOrderByCreatedAtDesc(siteId)
                        .map(SocietyLedger::getBalanceAfter)
                        .orElse(BigDecimal.ZERO);

        SocietyLedger entry = new SocietyLedger();

        entry.setSiteId(siteId);
        entry.setTransactionType("DEBIT");
        entry.setSource(source);
        entry.setReferenceId(referenceId);
        entry.setDescription(description);
        entry.setCredit(BigDecimal.ZERO);
        entry.setDebit(amount);
        entry.setBalanceAfter(lastBalance.subtract(amount));

        ledgerRepo.save(entry);
    }
}
