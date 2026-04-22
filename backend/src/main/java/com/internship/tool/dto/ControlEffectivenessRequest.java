package com.internship.tool.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ControlEffectivenessRequest {

    @NotBlank(message = "Control name is required")
    @Size(max = 255, message = "Control name must be under 255 characters")
    private String controlName;

    @Size(max = 5000, message = "Description must be under 5000 characters")
    private String controlDescription;

    @Size(max = 100, message = "Category must be under 100 characters")
    private String category;

    @Pattern(regexp = "^(LOW|MEDIUM|HIGH|CRITICAL)$", message = "Risk level must be LOW, MEDIUM, HIGH, or CRITICAL")
    private String riskLevel;

    @Min(value = 0, message = "Score must be between 0 and 100")
    @Max(value = 100, message = "Score must be between 0 and 100")
    private Integer effectivenessScore;

    @Pattern(regexp = "^(PENDING|IN_PROGRESS|COMPLETED|REVIEWED|ARCHIVED)$",
             message = "Status must be PENDING, IN_PROGRESS, COMPLETED, REVIEWED, or ARCHIVED")
    private String status;

    @Size(max = 100)
    private String assessor;

    @Size(max = 100)
    private String department;

    private LocalDate reviewDate;
}
