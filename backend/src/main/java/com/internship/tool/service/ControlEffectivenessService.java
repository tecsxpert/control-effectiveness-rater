package com.internship.tool.service;

import com.internship.tool.dto.ControlEffectivenessRequest;
import com.internship.tool.dto.ControlEffectivenessResponse;
import com.internship.tool.dto.StatsResponse;
import com.internship.tool.entity.ControlEffectiveness;
import com.internship.tool.entity.User;
import com.internship.tool.exception.BadRequestException;
import com.internship.tool.exception.ResourceNotFoundException;
import com.internship.tool.repository.ControlEffectivenessRepository;
import com.internship.tool.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ControlEffectivenessService {

    private static final Logger log = LoggerFactory.getLogger(ControlEffectivenessService.class);

    private final ControlEffectivenessRepository repository;
    private final UserRepository userRepository;
    private final AiServiceClient aiServiceClient;

    public ControlEffectivenessService(ControlEffectivenessRepository repository,
                                        UserRepository userRepository,
                                        AiServiceClient aiServiceClient) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.aiServiceClient = aiServiceClient;
    }

    @Cacheable(value = "controls", key = "'all-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ControlEffectivenessResponse> getAll(Pageable pageable) {
        return repository.findByIsDeletedFalse(pageable)
                .map(ControlEffectivenessResponse::fromEntity);
    }

    @Cacheable(value = "controls", key = "'id-' + #id")
    public ControlEffectivenessResponse getById(Long id) {
        ControlEffectiveness entity = repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Control", id));
        return ControlEffectivenessResponse.fromEntity(entity);
    }

    @CacheEvict(value = "controls", allEntries = true)
    @Transactional
    public ControlEffectivenessResponse create(ControlEffectivenessRequest request) {
        validateRequest(request);

        User currentUser = getCurrentUser();

        ControlEffectiveness entity = ControlEffectiveness.builder()
                .controlName(request.getControlName())
                .controlDescription(request.getControlDescription())
                .category(request.getCategory())
                .riskLevel(request.getRiskLevel() != null ? request.getRiskLevel() : "MEDIUM")
                .effectivenessScore(request.getEffectivenessScore())
                .status(request.getStatus() != null ? request.getStatus() : "PENDING")
                .assessor(request.getAssessor())
                .department(request.getDepartment())
                .reviewDate(request.getReviewDate())
                .createdBy(currentUser)
                .build();

        entity = repository.save(entity);

        // Trigger async AI description
        triggerAiDescription(entity.getId());

        return ControlEffectivenessResponse.fromEntity(entity);
    }

    @CacheEvict(value = "controls", allEntries = true)
    @Transactional
    public ControlEffectivenessResponse update(Long id, ControlEffectivenessRequest request) {
        validateRequest(request);

        ControlEffectiveness entity = repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Control", id));

        entity.setControlName(request.getControlName());
        entity.setControlDescription(request.getControlDescription());
        entity.setCategory(request.getCategory());
        if (request.getRiskLevel() != null) entity.setRiskLevel(request.getRiskLevel());
        entity.setEffectivenessScore(request.getEffectivenessScore());
        if (request.getStatus() != null) entity.setStatus(request.getStatus());
        entity.setAssessor(request.getAssessor());
        entity.setDepartment(request.getDepartment());
        entity.setReviewDate(request.getReviewDate());

        entity = repository.save(entity);
        return ControlEffectivenessResponse.fromEntity(entity);
    }

    @CacheEvict(value = "controls", allEntries = true)
    @Transactional
    public void softDelete(Long id) {
        ControlEffectiveness entity = repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Control", id));
        entity.setIsDeleted(true);
        repository.save(entity);
    }

    @Cacheable(value = "controls", key = "'search-' + #query + '-' + #pageable.pageNumber")
    public Page<ControlEffectivenessResponse> search(String query, Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            throw new BadRequestException("Search query cannot be empty");
        }
        return repository.searchByQuery(query.trim(), pageable)
                .map(ControlEffectivenessResponse::fromEntity);
    }

    public Page<ControlEffectivenessResponse> filter(String status, String category,
                                                      String riskLevel, LocalDate startDate,
                                                      LocalDate endDate, Pageable pageable) {
        return repository.findByFilters(status, category, riskLevel, startDate, endDate, pageable)
                .map(ControlEffectivenessResponse::fromEntity);
    }

    @Cacheable(value = "stats", key = "'dashboard'")
    public StatsResponse getStats() {
        long total = repository.countByIsDeletedFalse();
        long pending = repository.countByStatusAndIsDeletedFalse("PENDING");
        long completed = repository.countByStatusAndIsDeletedFalse("COMPLETED");
        Double avgScore = repository.findAverageEffectivenessScore();

        Map<String, Long> byRiskLevel = repository.countByRiskLevel().stream()
                .collect(Collectors.toMap(
                        r -> (String) r[0],
                        r -> (Long) r[1],
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        Map<String, Long> byStatus = repository.countByStatus().stream()
                .collect(Collectors.toMap(
                        r -> (String) r[0],
                        r -> (Long) r[1],
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        Map<String, Long> byCategory = repository.countByCategory().stream()
                .collect(Collectors.toMap(
                        r -> (String) r[0],
                        r -> (Long) r[1],
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        return StatsResponse.builder()
                .totalControls(total)
                .pendingControls(pending)
                .completedControls(completed)
                .averageScore(avgScore != null ? Math.round(avgScore * 100.0) / 100.0 : 0.0)
                .byRiskLevel(byRiskLevel)
                .byStatus(byStatus)
                .byCategory(byCategory)
                .build();
    }

    public List<ControlEffectivenessResponse> getAllForExport() {
        return repository.findByIsDeletedFalse().stream()
                .map(ControlEffectivenessResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Async
    public void triggerAiDescription(Long controlId) {
        try {
            ControlEffectiveness entity = repository.findById(controlId).orElse(null);
            if (entity == null) return;

            String aiResponse = aiServiceClient.describe(
                    entity.getControlName(),
                    entity.getControlDescription(),
                    entity.getCategory()
            );

            if (aiResponse != null) {
                entity.setAiDescription(aiResponse);
                entity.setIsFallback(false);
            } else {
                entity.setAiDescription("{\"description\": \"AI service temporarily unavailable. Please try again later.\", \"is_fallback\": true}");
                entity.setIsFallback(true);
            }
            repository.save(entity);
        } catch (Exception e) {
            log.error("Error triggering AI description for control {}: {}", controlId, e.getMessage());
        }
    }

    @CacheEvict(value = "controls", allEntries = true)
    @Transactional
    public ControlEffectivenessResponse triggerAiRecommendation(Long id) {
        ControlEffectiveness entity = repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Control", id));

        String aiResponse = aiServiceClient.recommend(
                entity.getControlName(),
                entity.getControlDescription(),
                entity.getRiskLevel(),
                entity.getEffectivenessScore()
        );

        if (aiResponse != null) {
            entity.setAiRecommendations(aiResponse);
            entity.setIsFallback(false);
        } else {
            entity.setAiRecommendations("{\"recommendations\": [], \"is_fallback\": true}");
            entity.setIsFallback(true);
        }

        entity = repository.save(entity);
        return ControlEffectivenessResponse.fromEntity(entity);
    }

    @CacheEvict(value = "controls", allEntries = true)
    @Transactional
    public ControlEffectivenessResponse triggerAiReport(Long id) {
        ControlEffectiveness entity = repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Control", id));

        String aiResponse = aiServiceClient.generateReport(
                entity.getControlName(),
                entity.getControlDescription(),
                entity.getCategory(),
                entity.getRiskLevel(),
                entity.getEffectivenessScore(),
                entity.getStatus()
        );

        if (aiResponse != null) {
            entity.setAiReport(aiResponse);
            entity.setIsFallback(false);
        } else {
            entity.setAiReport("{\"report\": \"AI report generation failed.\", \"is_fallback\": true}");
            entity.setIsFallback(true);
        }

        entity = repository.save(entity);
        return ControlEffectivenessResponse.fromEntity(entity);
    }

    private void validateRequest(ControlEffectivenessRequest request) {
        if (request.getControlName() == null || request.getControlName().trim().isEmpty()) {
            throw new BadRequestException("Control name is required");
        }
        if (request.getEffectivenessScore() != null &&
                (request.getEffectivenessScore() < 0 || request.getEffectivenessScore() > 100)) {
            throw new BadRequestException("Effectiveness score must be between 0 and 100");
        }
    }

    private User getCurrentUser() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            return userRepository.findByUsername(username).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}
