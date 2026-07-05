package com.apartment.maintenance.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "msg91")
public class Msg91Config {

    private boolean enabled;
    private String baseUrl;
    private String authKey;
    private String senderId;
    private String countryCode;
}