package com.internship.tool.dto;

import com.internship.tool.entity.ControlEffectiveness;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ControlEffectivenessResponse {

    private Long id;
    private String controlName;
    private String controlDescription;
    private String category;
    private String riskLevel;
    private Integer effectivenessScore;
    private String status;
    private String assessor;
    private String department;
    private LocalDate reviewDate;
    private String aiDescription;
    private String aiRecommendations;
    private String aiReport;
    private Boolean isFallback;
    private String createdByUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ControlEffectivenessResponse fromEntity(ControlEffectiveness entity) {
        return ControlEffectivenessResponse.builder()
                .id(entity.getId())
                .controlName(entity.getControlName())
                .controlDescription(entity.getControlDescription())
                .category(entity.getCategory())
                .riskLevel(entity.getRiskLevel())
                .effectivenessScore(entity.getEffectivenessScore())
                .status(entity.getStatus())
                .assessor(entity.getAssessor())
                .department(entity.getDepartment())
                .reviewDate(entity.getReviewDate())
                .aiDescription(entity.getAiDescription())
                .aiRecommendations(entity.getAiRecommendations())
                .aiReport(entity.getAiReport())
                .isFallback(entity.getIsFallback())
                .createdByUsername(entity.getCreatedBy() != null ? entity.getCreatedBy().getUsername() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
