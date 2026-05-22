package com.apartment.maintenance.service;


import com.apartment.maintenance.dto.SiteProfileDTO;
import com.apartment.maintenance.entity.User;
import com.apartment.maintenance.repository.SiteRepository;
import com.apartment.maintenance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepo;
    private final UserRepository userRepo;

    public SiteProfileDTO getMySite(UUID userId) {

        User user = userRepo.findById(userId)
                .orElseThrow();

        List<Object[]> result = siteRepo.getSiteProfile(user.getSiteId());

        if (result.isEmpty()) {
            throw new RuntimeException("Site not found");
        }

        Object[] row = result.get(0);

        return new SiteProfileDTO(
                (UUID) row[0],
                (String) row[1],
                (String) row[2],
                ((Number) row[3]).longValue()
        );
    }
}