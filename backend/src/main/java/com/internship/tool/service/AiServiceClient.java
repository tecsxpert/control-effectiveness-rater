package com.internship.tool.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class AiServiceClient {

    private static final Logger log = LoggerFactory.getLogger(AiServiceClient.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.ai-service.base-url}")
    private String aiBaseUrl;

    public AiServiceClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public String describe(String controlName, String controlDescription, String category) {
        Map<String, String> body = new HashMap<>();
        body.put("control_name", controlName);
        body.put("control_description", controlDescription);
        body.put("category", category);
        return callEndpoint("/describe", body);
    }

    public String recommend(String controlName, String controlDescription,
                            String riskLevel, Integer effectivenessScore) {
        Map<String, Object> body = new HashMap<>();
        body.put("control_name", controlName);
        body.put("control_description", controlDescription);
        body.put("risk_level", riskLevel);
        body.put("effectiveness_score", effectivenessScore);
        return callEndpoint("/recommend", body);
    }

    public String generateReport(String controlName, String controlDescription,
                                  String category, String riskLevel,
                                  Integer effectivenessScore, String status) {
        Map<String, Object> body = new HashMap<>();
        body.put("control_name", controlName);
        body.put("control_description", controlDescription);
        body.put("category", category);
        body.put("risk_level", riskLevel);
        body.put("effectiveness_score", effectivenessScore);
        body.put("status", status);
        return callEndpoint("/generate-report", body);
    }

    private String callEndpoint(String path, Object body) {
        int maxRetries = 3;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                String jsonBody = objectMapper.writeValueAsString(body);
                HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

                String response = restTemplate.postForObject(
                        aiBaseUrl + path, request, String.class);
                return response;
            } catch (Exception e) {
                log.error("AI service call to {} failed (attempt {}/{}): {}",
                        path, attempt, maxRetries, e.getMessage());
                if (attempt < maxRetries) {
                    try {
                        Thread.sleep((long) Math.pow(2, attempt) * 1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        log.error("AI service call to {} failed after {} retries — returning null", path, maxRetries);
        return null;
    }
}
