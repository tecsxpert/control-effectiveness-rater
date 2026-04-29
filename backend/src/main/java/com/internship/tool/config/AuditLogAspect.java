package com.internship.tool.config;

import com.internship.tool.entity.AuditLog;
import com.internship.tool.repository.AuditLogRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditLogAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditLogAspect.class);

    private final AuditLogRepository auditLogRepository;

    public AuditLogAspect(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @AfterReturning(
        pointcut = "execution(* com.internship.tool.service.ControlEffectivenessService.create(..))",
        returning = "result"
    )
    public void logCreate(JoinPoint joinPoint, Object result) {
        saveAuditLog("ControlEffectiveness", "CREATE", result);
    }

    @AfterReturning(
        pointcut = "execution(* com.internship.tool.service.ControlEffectivenessService.update(..))",
        returning = "result"
    )
    public void logUpdate(JoinPoint joinPoint, Object result) {
        saveAuditLog("ControlEffectiveness", "UPDATE", result);
    }

    @AfterReturning(
        pointcut = "execution(* com.internship.tool.service.ControlEffectivenessService.softDelete(..))"
    )
    public void logDelete(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Long id) {
            AuditLog auditLog = AuditLog.builder()
                    .entityType("ControlEffectiveness")
                    .entityId(id)
                    .action("DELETE")
                    .performedBy(getCurrentUsername())
                    .build();
            auditLogRepository.save(auditLog);
            log.info("Audit: DELETE on ControlEffectiveness id={} by {}", id, getCurrentUsername());
        }
    }

    private void saveAuditLog(String entityType, String action, Object result) {
        try {
            if (result != null) {
                var response = (com.internship.tool.dto.ControlEffectivenessResponse) result;
                AuditLog auditLog = AuditLog.builder()
                        .entityType(entityType)
                        .entityId(response.getId())
                        .action(action)
                        .performedBy(getCurrentUsername())
                        .newValue(response.getControlName())
                        .build();
                auditLogRepository.save(auditLog);
                log.info("Audit: {} on {} id={} by {}", action, entityType,
                        response.getId(), getCurrentUsername());
            }
        } catch (Exception e) {
            log.error("Failed to save audit log: {}", e.getMessage());
        }
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SYSTEM";
    }
}
