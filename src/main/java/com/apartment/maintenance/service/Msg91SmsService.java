package com.apartment.maintenance.service;

import com.apartment.maintenance.config.Msg91Config;
import com.apartment.maintenance.constants.SmsTemplate;
import com.apartment.maintenance.dto.msg91.Msg91Request;
import com.apartment.maintenance.entity.SmsLog;
import com.apartment.maintenance.repository.SmsLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Msg91SmsService implements SmsService {

    private static final Logger log = LoggerFactory.getLogger(Msg91SmsService.class);

    private final Msg91Config msg91Config;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final SmsLogRepository smsLogRepository;

    public Msg91SmsService(Msg91Config msg91Config,
                           RestTemplate restTemplate,
                           ObjectMapper objectMapper,
                           SmsLogRepository smsLogRepository) {
        this.msg91Config = msg91Config;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.smsLogRepository = smsLogRepository;
    }

    @Override
    public void sendSms(String mobileNumber, SmsTemplate template, Map<String, String> variables) {

        String formattedMobile = null;
        Msg91Request payload = null;
        String requestJson = null;

        try {
            formattedMobile = formatMobileNumber(mobileNumber);
            payload = buildRequest(formattedMobile, template, variables);
            requestJson = toJson(payload);
            log.info("MSG91 Payload : {}", requestJson);
            if (!msg91Config.isEnabled()) {
                log.info("MSG91 disabled. SMS skipped. Template: {}, Mobile: {}", template.name(), formattedMobile);
                saveSmsLog(formattedMobile, template, "SKIPPED", requestJson, "MSG91 disabled", null);
                return;
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            log.info("MSG91 authkey :",msg91Config.getAuthKey());
            headers.set("authkey", msg91Config.getAuthKey());

            HttpEntity<Msg91Request> request = new HttpEntity<>(payload, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    msg91Config.getBaseUrl(),
                    HttpMethod.POST,
                    request,
                    String.class
            );

            String responseBody = response.getBody();
            log.info("MSG91 SMS sent. Template: {}, Mobile: {}, Response: {}",
                    template.name(), formattedMobile, responseBody);

            saveSmsLog(formattedMobile, template, "SUCCESS", requestJson, responseBody, null);

        } catch (Exception e) {
            log.error("MSG91 SMS failed. Template: {}, Mobile: {}", template.name(), mobileNumber, e);
            saveSmsLog(
                    formattedMobile == null ? mobileNumber : formattedMobile,
                    template,
                    "FAILED",
                    requestJson,
                    null,
                    e.getMessage()
            );
        }
    }

    private Msg91Request buildRequest(String formattedMobile,
                                      SmsTemplate template,
                                      Map<String, String> variables) {

        Map<String, Object> recipient = new HashMap<>();
        recipient.put("mobiles", formattedMobile);

        if (variables != null && !variables.isEmpty()) {
            recipient.putAll(variables);
        }

        Msg91Request payload = new Msg91Request();
        payload.setTemplateId(template.getFlowId());
        payload.setSender(msg91Config.getSenderId());
        payload.setShortUrl("0");
        payload.setRecipients(List.of(recipient));

        return payload;
    }

    private String formatMobileNumber(String mobileNumber) {

        if (mobileNumber == null || mobileNumber.isBlank()) {
            throw new IllegalArgumentException("Mobile number cannot be empty");
        }

        String cleaned = mobileNumber.replaceAll("\\D", "");

        if (cleaned.length() == 10) {
            return msg91Config.getCountryCode() + cleaned;
        }

        if (cleaned.startsWith(msg91Config.getCountryCode())) {
            return cleaned;
        }

        return msg91Config.getCountryCode() + cleaned;
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private void saveSmsLog(String mobileNumber,
                            SmsTemplate template,
                            String status,
                            String requestPayload,
                            String responseBody,
                            String errorMessage) {
        try {
            SmsLog smsLog = new SmsLog();
            smsLog.setMobileNumber(mobileNumber == null ? "-" : mobileNumber);
            smsLog.setTemplateName(template.name());
            smsLog.setFlowId(template.getFlowId());
            smsLog.setStatus(status);
            smsLog.setRequestPayload(requestPayload);
            smsLog.setResponseBody(responseBody);
            smsLog.setErrorMessage(errorMessage);
            smsLogRepository.save(smsLog);
        } catch (Exception logException) {
            log.error("Failed to save SMS log. Template: {}, Mobile: {}", template.name(), mobileNumber, logException);
        }
    }
}
