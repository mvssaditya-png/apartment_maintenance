package com.apartment.maintenance.dto.msg91;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Msg91Request {

    @JsonProperty("template_id")
    private String templateId;

    private String sender;

    @JsonProperty("short_url")
    private String shortUrl = "0";

    private List<Map<String, Object>> recipients;
}