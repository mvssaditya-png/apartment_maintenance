package com.apartment.maintenance.constants;

public enum SmsTemplate {

    APP_LOGIN_OTP("6a49de2af67e0e93b606a165"),

    PAYMENT_REQUEST_CREATED("6a4b4eca243ce458170d94a2"),
    PAYMENT_DUE_REMINDER("6a4b4f74f01700aae80b0773"),
    PAYMENT_SUBMITTED("6a4b4f9d5136e0c2820a8e62"),
    PAYMENT_APPROVED("6a4b4f01feddf77c9109ac82"),
    PAYMENT_REJECTED("6a4b4f3ab32a3a4a830cc672"),
    DIRECT_PAYMENT_RECORDED("6a4b521f5af170bc1f04d872"),

    NOTICE_CREATED("6a4b4fc8648eb4267000cf82"),
    MEETING_CREATED("6a4b4e65ec54e3604a0aff13"),

    COMPLAINT_CREATED("6a4b503b57c8aa3df1082e72"),
    COMPLAINT_UPDATED("6a4b4ffd22ba721ee5074db2"),

    TRIAL_STARTED("6a4b506a7b35ceb867047e52"),
    TRIAL_EXPIRY_REMINDER("6a4b50d38617276c8006fe02"),
    TRIAL_EXPIRED("6a4b5137335cef6f4a055454"),

    SUBSCRIPTION_ACTIVATED("6a4b5169b4378077dc0e7172"),
    SUBSCRIPTION_EXPIRY_REMINDER("6a4b519208e72a7b85052da3"),
    SUBSCRIPTION_EXPIRED("6a4b51b8733ead0eb40daa72");

    private final String flowId;

    SmsTemplate(String flowId) {
        this.flowId = flowId;
    }

    public String getFlowId() {
        return flowId;
    }
}