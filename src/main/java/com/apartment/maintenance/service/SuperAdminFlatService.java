package com.apartment.maintenance.service;

import com.apartment.maintenance.dto.SuperAdminFlatRequest;
import com.apartment.maintenance.dto.SuperAdminFlatResponse;
import com.apartment.maintenance.entity.Flat;
import com.apartment.maintenance.entity.Site;
import com.apartment.maintenance.entity.User;
import com.apartment.maintenance.repository.FlatRepository;
import com.apartment.maintenance.repository.SiteRepository;
import com.apartment.maintenance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SuperAdminFlatService {

    private final FlatRepository flatRepository;
    private final SiteRepository siteRepository;
    private final UserRepository userRepository;

    public List<SuperAdminFlatResponse> getFlats(UUID siteId) {
        ensureSiteExists(siteId);

        return flatRepository.findBySiteIdOrderByFlatNumberAsc(siteId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public SuperAdminFlatResponse addFlat(UUID siteId, SuperAdminFlatRequest request) {

        Site site = ensureSiteExists(siteId);

        String flatNumber = clean(request.getFlatNumber());
        String ownerName = clean(request.getOwnerName());
        String ownerPhone = clean(request.getOwnerPhone());
        String ownerEmail = clean(request.getOwnerEmail());
        String role = clean(request.getRole()).toUpperCase();

        validateFlatRequest(siteId, flatNumber, ownerName, ownerPhone, role);

        if (flatRepository.existsBySiteIdAndFlatNumberIgnoreCase(siteId, flatNumber)) {
            throw new RuntimeException("Flat number already exists");
        }

        if (userRepository.existsByPhoneNumber(ownerPhone)) {
            throw new RuntimeException("Owner phone number already exists");
        }

        Flat flat = new Flat();
        flat.setSiteId(siteId);
        flat.setFlatNumber(flatNumber);
        flat.setOwnerName(ownerName);
        flat.setOwnerPhone(ownerPhone);
        flat.setActive(true);

        Flat savedFlat = flatRepository.save(flat);

        User ownerUser = new User();
        ownerUser.setSiteId(siteId);
        ownerUser.setFlatId(savedFlat.getFlatId());
        ownerUser.setFlatNumber(savedFlat.getFlatNumber());
        ownerUser.setName(ownerName);
        ownerUser.setPhoneNumber(ownerPhone);
        ownerUser.setEmail(ownerEmail.isBlank() ? null : ownerEmail);
        ownerUser.setRole(role);
        ownerUser.setResidentType("OWNER");
        ownerUser.setIsActive(true);

        userRepository.save(ownerUser);

        updateTotalFlats(site);

        return mapToResponse(savedFlat);
    }

    @Transactional
    public SuperAdminFlatResponse updateFlat(UUID flatId, SuperAdminFlatRequest request) {

        Flat flat = flatRepository.findById(flatId)
                .orElseThrow(() -> new RuntimeException("Flat not found"));

        String flatNumber = clean(request.getFlatNumber());
        String ownerName = clean(request.getOwnerName());
        String ownerPhone = clean(request.getOwnerPhone());
        String ownerEmail = clean(request.getOwnerEmail());
        String role = clean(request.getRole()).toUpperCase();

        validateFlatRequest(flat.getSiteId(), flatNumber, ownerName, ownerPhone, role);

        boolean duplicate =
                flatRepository.existsBySiteIdAndFlatNumberIgnoreCase(flat.getSiteId(), flatNumber)
                        && !flat.getFlatNumber().equalsIgnoreCase(flatNumber);

        if (duplicate) {
            throw new RuntimeException("Flat number already exists");
        }

        User ownerUser =
                userRepository.findByFlatIdAndResidentType(flat.getFlatId(), "OWNER")
                        .orElse(null);

        if (ownerUser != null
                && !ownerUser.getPhoneNumber().equals(ownerPhone)
                && userRepository.existsByPhoneNumber(ownerPhone)) {
            throw new RuntimeException("Owner phone number already exists");
        }

        flat.setFlatNumber(flatNumber);
        flat.setOwnerName(ownerName);
        flat.setOwnerPhone(ownerPhone);

        Flat savedFlat = flatRepository.save(flat);

        if (ownerUser == null) {
            ownerUser = new User();
            ownerUser.setSiteId(savedFlat.getSiteId());
            ownerUser.setFlatId(savedFlat.getFlatId());
            ownerUser.setResidentType("OWNER");
            ownerUser.setIsActive(true);
        }

        ownerUser.setFlatNumber(savedFlat.getFlatNumber());
        ownerUser.setName(ownerName);
        ownerUser.setPhoneNumber(ownerPhone);
        ownerUser.setEmail(ownerEmail.isBlank() ? null : ownerEmail);
        ownerUser.setRole(role);

        userRepository.save(ownerUser);

        return mapToResponse(savedFlat);
    }

    @Transactional
    public SuperAdminFlatResponse toggleFlat(UUID flatId) {

        Flat flat = flatRepository.findById(flatId)
                .orElseThrow(() -> new RuntimeException("Flat not found"));

        flat.setActive(!Boolean.TRUE.equals(flat.getActive()));

        Flat savedFlat = flatRepository.save(flat);

        List<User> users = userRepository.findByFlatId(flatId);

        for (User user : users) {
            user.setIsActive(savedFlat.getActive());
        }

        userRepository.saveAll(users);

        Site site = siteRepository.findById(flat.getSiteId())
                .orElseThrow(() -> new RuntimeException("Site not found"));

        updateTotalFlats(site);

        return mapToResponse(savedFlat);
    }

    private void validateFlatRequest(
            UUID siteId,
            String flatNumber,
            String ownerName,
            String ownerPhone,
            String role
    ) {
        if (flatNumber.isBlank()) {
            throw new RuntimeException("Flat number is required");
        }

        if (ownerName.isBlank()) {
            throw new RuntimeException("Owner name is required");
        }

        if (ownerPhone.isBlank()) {
            throw new RuntimeException("Owner phone number is required");
        }

        if (!ownerPhone.matches("\\d{10}")) {
            throw new RuntimeException("Owner phone number must be 10 digits");
        }

        if (!role.equals("ADMIN")
                && !role.equals("CASHIER")
                && !role.equals("RESIDENT")) {
            throw new RuntimeException("Invalid role. Allowed roles are ADMIN, CASHIER, RESIDENT");
        }
    }

    private Site ensureSiteExists(UUID siteId) {
        return siteRepository.findById(siteId)
                .orElseThrow(() -> new RuntimeException("Apartment not found"));
    }

    private void updateTotalFlats(Site site) {
        long activeFlatCount =
                flatRepository.countBySiteIdAndIsActiveTrue(site.getSiteId());

        site.setTotalFlats((int) activeFlatCount);
        siteRepository.save(site);
    }

    private SuperAdminFlatResponse mapToResponse(Flat flat) {

        User owner =
                userRepository.findByFlatIdAndResidentType(flat.getFlatId(), "OWNER")
                        .orElse(null);

        return SuperAdminFlatResponse.builder()
                .flatId(flat.getFlatId())
                .siteId(flat.getSiteId())
                .flatNumber(flat.getFlatNumber())
                .ownerName(flat.getOwnerName())
                .ownerPhone(flat.getOwnerPhone())
                .ownerEmail(owner != null ? owner.getEmail() : null)
                .role(owner != null ? owner.getRole() : null)
                .active(flat.getActive())
                .build();
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}