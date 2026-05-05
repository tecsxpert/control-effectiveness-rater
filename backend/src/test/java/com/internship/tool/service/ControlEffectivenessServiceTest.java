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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControlEffectivenessServiceTest {

    @Mock
    private ControlEffectivenessRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AiServiceClient aiServiceClient;

    @InjectMocks
    private ControlEffectivenessService service;

    private ControlEffectiveness sampleEntity;
    private ControlEffectivenessRequest sampleRequest;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .username("admin")
                .email("admin@test.com")
                .password("encoded")
                .role("ADMIN")
                .build();

        sampleEntity = ControlEffectiveness.builder()
                .id(1L)
                .controlName("Firewall Rule Review")
                .controlDescription("Quarterly review of firewall rules")
                .category("Network Security")
                .riskLevel("HIGH")
                .effectivenessScore(78)
                .status("COMPLETED")
                .assessor("Sarah Chen")
                .department("IT Security")
                .reviewDate(LocalDate.of(2026, 3, 15))
                .isDeleted(false)
                .isFallback(false)
                .createdBy(sampleUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleRequest = ControlEffectivenessRequest.builder()
                .controlName("Firewall Rule Review")
                .controlDescription("Quarterly review of firewall rules")
                .category("Network Security")
                .riskLevel("HIGH")
                .effectivenessScore(78)
                .status("COMPLETED")
                .assessor("Sarah Chen")
                .department("IT Security")
                .reviewDate(LocalDate.of(2026, 3, 15))
                .build();
    }

    @Test
    @DisplayName("1. getAll — returns paginated results")
    void testGetAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ControlEffectiveness> page = new PageImpl<>(List.of(sampleEntity));
        when(repository.findByIsDeletedFalse(pageable)).thenReturn(page);

        Page<ControlEffectivenessResponse> result = service.getAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Firewall Rule Review", result.getContent().get(0).getControlName());
        verify(repository).findByIsDeletedFalse(pageable);
    }

    @Test
    @DisplayName("2. getById — returns control when found")
    void testGetById_found() {
        when(repository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(sampleEntity));

        ControlEffectivenessResponse result = service.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Firewall Rule Review", result.getControlName());
    }

    @Test
    @DisplayName("3. getById — throws ResourceNotFoundException when not found")
    void testGetById_notFound() {
        when(repository.findByIdAndIsDeletedFalse(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(999L));
    }

    @Test
    @DisplayName("4. create — saves and returns new control")
    void testCreate() {
        mockSecurityContext("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(sampleUser));
        when(repository.save(any(ControlEffectiveness.class))).thenReturn(sampleEntity);

        ControlEffectivenessResponse result = service.create(sampleRequest);

        assertNotNull(result);
        assertEquals("Firewall Rule Review", result.getControlName());
        verify(repository).save(any(ControlEffectiveness.class));
    }

    @Test
    @DisplayName("5. create — throws BadRequestException for blank control name")
    void testCreate_blankName() {
        sampleRequest.setControlName("");

        assertThrows(BadRequestException.class, () -> service.create(sampleRequest));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("6. create — throws BadRequestException for invalid score")
    void testCreate_invalidScore() {
        sampleRequest.setEffectivenessScore(150);

        assertThrows(BadRequestException.class, () -> service.create(sampleRequest));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("7. update — updates existing control")
    void testUpdate() {
        when(repository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(sampleEntity));
        when(repository.save(any(ControlEffectiveness.class))).thenReturn(sampleEntity);

        sampleRequest.setControlName("Updated Firewall Review");
        ControlEffectivenessResponse result = service.update(1L, sampleRequest);

        assertNotNull(result);
        verify(repository).save(any(ControlEffectiveness.class));
    }

    @Test
    @DisplayName("8. softDelete — marks control as deleted")
    void testSoftDelete() {
        when(repository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(sampleEntity));
        when(repository.save(any(ControlEffectiveness.class))).thenReturn(sampleEntity);

        service.softDelete(1L);

        assertTrue(sampleEntity.getIsDeleted());
        verify(repository).save(sampleEntity);
    }

    @Test
    @DisplayName("9. search — throws BadRequestException for empty query")
    void testSearch_emptyQuery() {
        Pageable pageable = PageRequest.of(0, 10);

        assertThrows(BadRequestException.class, () -> service.search("", pageable));
    }

    @Test
    @DisplayName("10. getStats — returns dashboard statistics")
    void testGetStats() {
        when(repository.countByIsDeletedFalse()).thenReturn(15L);
        when(repository.countByStatusAndIsDeletedFalse("PENDING")).thenReturn(3L);
        when(repository.countByStatusAndIsDeletedFalse("COMPLETED")).thenReturn(6L);
        when(repository.findAverageEffectivenessScore()).thenReturn(72.5);
        when(repository.countByRiskLevel()).thenReturn(List.<Object[]>of(
                new Object[]{"HIGH", 5L}, new Object[]{"MEDIUM", 4L}
        ));
        when(repository.countByStatus()).thenReturn(List.<Object[]>of(
                new Object[]{"COMPLETED", 6L}, new Object[]{"PENDING", 3L}
        ));
        when(repository.countByCategory()).thenReturn(List.<Object[]>of(
                new Object[]{"Network Security", 3L}
        ));

        StatsResponse stats = service.getStats();

        assertEquals(15L, stats.getTotalControls());
        assertEquals(3L, stats.getPendingControls());
        assertEquals(6L, stats.getCompletedControls());
        assertEquals(72.5, stats.getAverageScore());
    }

    @Test
    @DisplayName("11. update — throws ResourceNotFoundException when control not found")
    void testUpdate_notFound() {
        when(repository.findByIdAndIsDeletedFalse(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(999L, sampleRequest));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("12. softDelete — throws ResourceNotFoundException when control not found")
    void testSoftDelete_notFound() {
        when(repository.findByIdAndIsDeletedFalse(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.softDelete(999L));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("13. search — returns results for a valid query")
    void testSearch_validQuery() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ControlEffectiveness> page = new PageImpl<>(List.of(sampleEntity));
        when(repository.searchByQuery("firewall", pageable)).thenReturn(page);

        Page<ControlEffectivenessResponse> result = service.search("firewall", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Firewall Rule Review", result.getContent().get(0).getControlName());
        verify(repository).searchByQuery("firewall", pageable);
    }

    @Test
    @DisplayName("14. filter — returns filtered results by status")
    void testFilter_byStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ControlEffectiveness> page = new PageImpl<>(List.of(sampleEntity));
        when(repository.findByFilters("COMPLETED", null, null, null, null, pageable)).thenReturn(page);

        Page<ControlEffectivenessResponse> result = service.filter("COMPLETED", null, null, null, null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("COMPLETED", result.getContent().get(0).getStatus());
    }

    @Test
    @DisplayName("15. getAllForExport — returns all non-deleted controls as a list")
    void testGetAllForExport() {
        when(repository.findByIsDeletedFalse()).thenReturn(List.of(sampleEntity));

        List<ControlEffectivenessResponse> result = service.getAllForExport();

        assertEquals(1, result.size());
        assertEquals("Firewall Rule Review", result.get(0).getControlName());
        verify(repository).findByIsDeletedFalse();
    }

    @Test
    @DisplayName("16. triggerAiRecommendation — stores AI response when AI returns successfully")
    void testTriggerAiRecommendation_success() {
        when(repository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(sampleEntity));
        when(aiServiceClient.recommend(anyString(), anyString(), anyString(), any(Integer.class)))
                .thenReturn("{\"recommendations\":[]}");
        when(repository.save(any(ControlEffectiveness.class))).thenReturn(sampleEntity);

        ControlEffectivenessResponse result = service.triggerAiRecommendation(1L);

        assertNotNull(result);
        assertFalse(sampleEntity.getIsFallback());
        verify(aiServiceClient).recommend(anyString(), anyString(), anyString(), any(Integer.class));
    }

    @Test
    @DisplayName("17. triggerAiRecommendation — throws ResourceNotFoundException when control not found")
    void testTriggerAiRecommendation_notFound() {
        when(repository.findByIdAndIsDeletedFalse(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.triggerAiRecommendation(999L));
    }

    @Test
    @DisplayName("18. triggerAiRecommendation — sets isFallback=true when AI returns null")
    void testTriggerAiRecommendation_aiFallback() {
        when(repository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(sampleEntity));
        when(aiServiceClient.recommend(anyString(), anyString(), anyString(), any(Integer.class)))
                .thenReturn(null);
        when(repository.save(any(ControlEffectiveness.class))).thenReturn(sampleEntity);

        ControlEffectivenessResponse result = service.triggerAiRecommendation(1L);

        assertNotNull(result);
        assertTrue(sampleEntity.getIsFallback());
    }

    @Test
    @DisplayName("19. triggerAiReport — stores AI report when AI returns successfully")
    void testTriggerAiReport_success() {
        when(repository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(sampleEntity));
        when(aiServiceClient.generateReport(anyString(), anyString(), anyString(),
                anyString(), any(Integer.class), anyString()))
                .thenReturn("{\"title\":\"Report\",\"summary\":\"Test summary\"}");
        when(repository.save(any(ControlEffectiveness.class))).thenReturn(sampleEntity);

        ControlEffectivenessResponse result = service.triggerAiReport(1L);

        assertNotNull(result);
        assertFalse(sampleEntity.getIsFallback());
        verify(aiServiceClient).generateReport(anyString(), anyString(), anyString(),
                anyString(), any(Integer.class), anyString());
    }

    @Test
    @DisplayName("20. triggerAiReport — throws ResourceNotFoundException when control not found")
    void testTriggerAiReport_notFound() {
        when(repository.findByIdAndIsDeletedFalse(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.triggerAiReport(999L));
    }

    private void mockSecurityContext(String username) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(username);
        SecurityContext secCtx = mock(SecurityContext.class);
        when(secCtx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(secCtx);
    }
}
