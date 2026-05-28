package com.apartment.maintenance.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class MyDueResponse {

    private UUID paymentId;
    private UUID siteId;
    private UUID flatId;
    private Integer paymentMonth;
    private Integer paymentYear;
    private BigDecimal amount;
    private String paymentStatus;
    private String paymentMode;
    private LocalDateTime paymentDate;
    private LocalDateTime approvedAt;
    private String receiptUrl;
    private LocalDateTime createdAt;
    private UUID requestId;
    private String requestType;
    private String requestTitle;
    private String description;
    private String receiptNumber;
    private String receiptPdfUrl;

    public MyDueResponse(
            UUID paymentId,
            UUID siteId,
            UUID flatId,
            Integer paymentMonth,
            Integer paymentYear,
            BigDecimal amount,
            String paymentStatus,
            String paymentMode,
            LocalDateTime paymentDate,
            LocalDateTime approvedAt,
            String receiptUrl,
            LocalDateTime createdAt,
            UUID requestId,
            String requestType,
            String requestTitle,
            String description,
            String receiptNumber,
            String receiptPdfUrl
    ) {
        this.paymentId = paymentId;
        this.siteId = siteId;
        this.flatId = flatId;
        this.paymentMonth = paymentMonth;
        this.paymentYear = paymentYear;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.paymentMode = paymentMode;
        this.paymentDate = paymentDate;
        this.approvedAt = approvedAt;
        this.receiptUrl = receiptUrl;
        this.createdAt = createdAt;
        this.requestId = requestId;
        this.requestType = requestType;
        this.requestTitle = requestTitle;
        this.description = description;
        this.receiptNumber = receiptNumber;
        this.receiptPdfUrl = receiptPdfUrl;
    }

    public UUID getPaymentId() { return paymentId; }
    public UUID getSiteId() { return siteId; }
    public UUID getFlatId() { return flatId; }
    public Integer getPaymentMonth() { return paymentMonth; }
    public Integer getPaymentYear() { return paymentYear; }
    public BigDecimal getAmount() { return amount; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getPaymentMode() { return paymentMode; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public String getReceiptUrl() { return receiptUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public UUID getRequestId() { return requestId; }
    public String getRequestType() { return requestType; }
    public String getRequestTitle() { return requestTitle; }
    public String getDescription() { return description; }
    public String getReceiptNumber() { return receiptNumber; }
    public String getReceiptPdfUrl() { return receiptPdfUrl; }
}