package com.apartment.maintenance.controller;

import com.apartment.maintenance.entity.SocietyLedger;
import com.apartment.maintenance.repository.SocietyLedgerRepository;
import com.apartment.maintenance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ledger")
@RequiredArgsConstructor
public class LedgerController {

    private final SocietyLedgerRepository repo;
    private final UserRepository userRepo;

    @GetMapping
    public List<SocietyLedger> getLedger(
            @AuthenticationPrincipal UUID userId) {

        UUID siteId =
                userRepo.findById(userId)
                        .orElseThrow()
                        .getSiteId();

        return repo.findAll()
                .stream()
                .filter(l -> l.getSiteId().equals(siteId))
                .toList();
    }
}
