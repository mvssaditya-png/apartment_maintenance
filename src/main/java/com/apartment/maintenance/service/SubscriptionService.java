package com.apartment.maintenance.service;

import com.apartment.maintenance.dto.SubscriptionPlanResponse;
import com.apartment.maintenance.dto.SubscriptionStatusResponse;
import com.apartment.maintenance.entity.Site;
import com.apartment.maintenance.entity.User;
import com.apartment.maintenance.repository.SiteRepository;
import com.apartment.maintenance.repository.SubscriptionPlanRepository;
import com.apartment.maintenance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import com.apartment.maintenance.dto.CreateSubscriptionOrderRequest;
import com.apartment.maintenance.dto.CreateSubscriptionOrderResponse;
import com.apartment.maintenance.entity.SiteSubscription;
import com.apartment.maintenance.entity.SubscriptionPlan;
import com.apartment.maintenance.repository.SiteSubscriptionRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.apartment.maintenance.dto.VerifySubscriptionPaymentRequest;
import com.apartment.maintenance.dto.VerifySubscriptionPaymentResponse;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final UserRepository userRepository;
    private final SiteRepository siteRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    @Value("${razorpay.key-id}")
    private String razorpayKeyId;

    @Value("${razorpay.key-secret}")
    private String razorpayKeySecret;

    private final SiteSubscriptionRepository siteSubscriptionRepository;
    public SubscriptionStatusResponse getStatus(UUID userId) {

        User user = userRepository.findById(userId).orElseThrow();

        if ("SUPER_ADMIN".equalsIgnoreCase(user.getRole())) {
            return SubscriptionStatusResponse.builder()
                    .status("SUPER_ADMIN")
                    .allowed(true)
                    .trial(false)
                    .expired(false)
                    .daysRemaining(null)
                    .message("Super Admin access allowed")
                    .build();
        }

        Site site = siteRepository.findById(user.getSiteId()).orElseThrow();

        if (Boolean.FALSE.equals(site.getIsActive())) {
            return SubscriptionStatusResponse.builder()
                    .status("INACTIVE")
                    .allowed(false)
                    .trial(false)
                    .expired(true)
                    .daysRemaining(0L)
                    .message("Apartment is inactive")
                    .build();
        }

        LocalDate today = LocalDate.now();

        if ("TRIAL".equalsIgnoreCase(site.getSubscriptionStatus())) {

            if (site.getTrialEndDate() != null
                    && !today.isAfter(site.getTrialEndDate())) {

                long daysRemaining =
                        ChronoUnit.DAYS.between(today, site.getTrialEndDate());

                return SubscriptionStatusResponse.builder()
                        .status("TRIAL")
                        .allowed(true)
                        .trial(true)
                        .expired(false)
                        .daysRemaining(daysRemaining)
                        .trialEndDate(site.getTrialEndDate())
                        .message("Free trial active")
                        .build();
            }

            site.setSubscriptionStatus("EXPIRED");
            siteRepository.save(site);

            return expiredResponse(user);
        }

        if ("ACTIVE".equalsIgnoreCase(site.getSubscriptionStatus())) {

            if (site.getSubscriptionEndDate() != null
                    && !today.isAfter(site.getSubscriptionEndDate())) {

                long daysRemaining =
                        ChronoUnit.DAYS.between(today, site.getSubscriptionEndDate());

                return SubscriptionStatusResponse.builder()
                        .status("ACTIVE")
                        .allowed(true)
                        .trial(false)
                        .expired(false)
                        .daysRemaining(daysRemaining)
                        .subscriptionEndDate(site.getSubscriptionEndDate())
                        .message("Subscription active")
                        .build();
            }

            site.setSubscriptionStatus("EXPIRED");
            siteRepository.save(site);

            return expiredResponse(user);
        }

        return expiredResponse(user);
    }

    private SubscriptionStatusResponse expiredResponse(User user) {

        boolean isAdmin =
                "ADMIN".equalsIgnoreCase(user.getRole());

        return SubscriptionStatusResponse.builder()
                .status("EXPIRED")
                .allowed(isAdmin)
                .trial(false)
                .expired(true)
                .daysRemaining(0L)
                .message(
                        isAdmin
                                ? "Subscription expired. Please renew to continue."
                                : "Apartment subscription expired. Please contact admin."
                )
                .build();
    }

    public List<SubscriptionPlanResponse> getPlans(UUID userId) {

        User user = userRepository.findById(userId).orElseThrow();

        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("Only admin can view subscription plans");
        }

        Site site = siteRepository.findById(user.getSiteId()).orElseThrow();

        Integer flatCount =
                site.getTotalFlats() == null || site.getTotalFlats() <= 0
                        ? 1
                        : site.getTotalFlats();

        return subscriptionPlanRepository.findPlansForFlatCount(flatCount)
                .stream()
                .map(plan -> SubscriptionPlanResponse.builder()
                        .planId(plan.getPlanId())
                        .minFlats(plan.getMinFlats())
                        .maxFlats(plan.getMaxFlats())
                        .durationMonths(plan.getDurationMonths())
                        .amount(plan.getAmount())
                        .label(plan.getDurationMonths() + " Months - ₹" + plan.getAmount())
                        .build()
                )
                .toList();
    }

    public CreateSubscriptionOrderResponse createOrder(
            UUID userId,
            CreateSubscriptionOrderRequest request
    ) {
        try {
            User user = userRepository.findById(userId).orElseThrow();

            if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
                throw new RuntimeException("Only admin can subscribe");
            }

            Site site = siteRepository.findById(user.getSiteId()).orElseThrow();

            SubscriptionPlan plan =
                    subscriptionPlanRepository.findById(request.getPlanId())
                            .orElseThrow();

            Integer flatCount =
                    site.getTotalFlats() == null || site.getTotalFlats() <= 0
                            ? 1
                            : site.getTotalFlats();

            if (!Boolean.TRUE.equals(plan.getActive())
                    || plan.getMinFlats() > flatCount
                    || (plan.getMaxFlats() != null && plan.getMaxFlats() < flatCount)) {
                throw new RuntimeException("Invalid subscription plan");
            }

            Integer amountInPaise =
                    plan.getAmount()
                            .multiply(BigDecimal.valueOf(100))
                            .intValue();

            RazorpayClient razorpayClient =
                    new RazorpayClient(
                            razorpayKeyId,
                            razorpayKeySecret
                    );

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put(
                    "receipt",
                    "sub_" + System.currentTimeMillis()
            );

            Order order =
                    razorpayClient.orders.create(orderRequest);

            SiteSubscription subscription =
                    new SiteSubscription();

            subscription.setSubscriptionId(UUID.randomUUID());
            subscription.setSiteId(site.getSiteId());
            subscription.setPlanId(plan.getPlanId());
            subscription.setFlatCount(flatCount);
            subscription.setAmount(plan.getAmount());
            subscription.setDurationMonths(plan.getDurationMonths());
            subscription.setStatus("PENDING");
            subscription.setRazorpayOrderId(order.get("id"));

            siteSubscriptionRepository.save(subscription);

            return CreateSubscriptionOrderResponse.builder()
                    .subscriptionId(subscription.getSubscriptionId())
                    .planId(plan.getPlanId())
                    .razorpayOrderId(order.get("id"))
                    .amount(plan.getAmount())
                    .amountInPaise(amountInPaise)
                    .currency("INR")
                    .keyId(razorpayKeyId)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to create subscription order: " + e.getMessage(),
                    e
            );
        }
    }
    @Transactional
    public VerifySubscriptionPaymentResponse verifyPayment(
            UUID userId,
            VerifySubscriptionPaymentRequest request
    ) {
        try {
            User user = userRepository.findById(userId).orElseThrow();

            if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
                throw new RuntimeException("Only admin can verify subscription payment");
            }

            SiteSubscription subscription =
                    siteSubscriptionRepository
                            .findByRazorpayOrderId(request.getRazorpayOrderId())
                            .orElseThrow(() -> new RuntimeException("Subscription order not found"));

            if (!subscription.getSiteId().equals(user.getSiteId())) {
                throw new RuntimeException("Invalid subscription order");
            }

            if ("PAID".equalsIgnoreCase(subscription.getStatus())) {
                Site site = siteRepository.findById(subscription.getSiteId()).orElseThrow();

                return VerifySubscriptionPaymentResponse.builder()
                        .status("ACTIVE")
                        .message("Subscription already active")
                        .subscriptionStartDate(site.getSubscriptionStartDate())
                        .subscriptionEndDate(site.getSubscriptionEndDate())
                        .build();
            }

            boolean validSignature =
                    verifyRazorpaySignature(
                            request.getRazorpayOrderId(),
                            request.getRazorpayPaymentId(),
                            request.getRazorpaySignature()
                    );

            if (!validSignature) {
                throw new RuntimeException("Invalid payment signature");
            }

            LocalDate today = LocalDate.now();

            Site site =
                    siteRepository.findById(subscription.getSiteId())
                            .orElseThrow();

            LocalDate startDate =
                    site.getSubscriptionEndDate() != null
                            && !today.isAfter(site.getSubscriptionEndDate())
                            ? site.getSubscriptionEndDate().plusDays(1)
                            : today;

            LocalDate endDate =
                    startDate.plusMonths(subscription.getDurationMonths());

            subscription.setRazorpayPaymentId(request.getRazorpayPaymentId());
            subscription.setRazorpaySignature(request.getRazorpaySignature());
            subscription.setStartDate(startDate);
            subscription.setEndDate(endDate);
            subscription.setStatus("PAID");

            siteSubscriptionRepository.save(subscription);

            site.setSubscriptionStatus("ACTIVE");
            site.setSubscriptionStartDate(startDate);
            site.setSubscriptionEndDate(endDate);

            siteRepository.save(site);

            return VerifySubscriptionPaymentResponse.builder()
                    .status("ACTIVE")
                    .message("Subscription activated successfully")
                    .subscriptionStartDate(startDate)
                    .subscriptionEndDate(endDate)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to verify payment: " + e.getMessage(),
                    e
            );
        }
    }

    private boolean verifyRazorpaySignature(
            String orderId,
            String paymentId,
            String signature
    ) {
        try {
            String payload = orderId + "|" + paymentId;

            Mac mac = Mac.getInstance("HmacSHA256");

            SecretKeySpec secretKeySpec =
                    new SecretKeySpec(
                            razorpayKeySecret.getBytes(StandardCharsets.UTF_8),
                            "HmacSHA256"
                    );

            mac.init(secretKeySpec);

            byte[] hash =
                    mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));

            String generatedSignature = bytesToHex(hash);

            return MessageDigest.isEqual(
                    generatedSignature.getBytes(StandardCharsets.UTF_8),
                    signature.getBytes(StandardCharsets.UTF_8)
            );

        } catch (Exception e) {
            throw new RuntimeException("Signature verification failed", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();

        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }

        return result.toString();
    }
}