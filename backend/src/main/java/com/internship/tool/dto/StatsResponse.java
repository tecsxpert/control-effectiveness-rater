package com.internship.tool.dto;

import lombok.*;

import java.util.Map;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class StatsResponse {
    private long totalControls;
    private long pendingControls;
    private long completedControls;
    private Double averageScore;
    private Map<String, Long> byRiskLevel;
    private Map<String, Long> byStatus;
    private Map<String, Long> byCategory;
}
