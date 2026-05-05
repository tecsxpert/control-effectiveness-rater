package com.internship.tool.repository;

import com.internship.tool.entity.ControlEffectiveness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ControlEffectivenessRepository extends JpaRepository<ControlEffectiveness, Long> {

    Page<ControlEffectiveness> findByIsDeletedFalse(Pageable pageable);

    Optional<ControlEffectiveness> findByIdAndIsDeletedFalse(Long id);

    @Query("SELECT c FROM ControlEffectiveness c WHERE c.isDeleted = false AND " +
           "(LOWER(c.controlName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.controlDescription) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.category) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.department) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<ControlEffectiveness> searchByQuery(@Param("query") String query, Pageable pageable);

    @Query("SELECT c FROM ControlEffectiveness c WHERE c.isDeleted = false " +
           "AND (:status IS NULL OR c.status = :status) " +
           "AND (:category IS NULL OR c.category = :category) " +
           "AND (:riskLevel IS NULL OR c.riskLevel = :riskLevel) " +
           "AND (:startDate IS NULL OR c.createdAt >= CAST(:startDate AS timestamp)) " +
           "AND (:endDate IS NULL OR c.createdAt <= CAST(:endDate AS timestamp))")
    Page<ControlEffectiveness> findByFilters(
            @Param("status") String status,
            @Param("category") String category,
            @Param("riskLevel") String riskLevel,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    long countByIsDeletedFalse();

    long countByStatusAndIsDeletedFalse(String status);

    @Query("SELECT AVG(c.effectivenessScore) FROM ControlEffectiveness c WHERE c.isDeleted = false AND c.effectivenessScore IS NOT NULL")
    Double findAverageEffectivenessScore();

    @Query("SELECT c.riskLevel, COUNT(c) FROM ControlEffectiveness c WHERE c.isDeleted = false GROUP BY c.riskLevel")
    List<Object[]> countByRiskLevel();

    @Query("SELECT c.status, COUNT(c) FROM ControlEffectiveness c WHERE c.isDeleted = false GROUP BY c.status")
    List<Object[]> countByStatus();

    @Query("SELECT c.category, COUNT(c) FROM ControlEffectiveness c WHERE c.isDeleted = false GROUP BY c.category")
    List<Object[]> countByCategory();

    List<ControlEffectiveness> findByIsDeletedFalseAndReviewDateBefore(LocalDate date);

    List<ControlEffectiveness> findByIsDeletedFalse();
}
