package com.apartment.maintenance.service;

import com.apartment.maintenance.constants.SmsTemplate;

import java.util.Map;

public interface SmsService {

    void sendSms(String mobileNumber, SmsTemplate template, Map<String, String> variables);
}