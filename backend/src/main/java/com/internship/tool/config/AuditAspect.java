package com.internship.tool.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Aspect
@Component
public class AuditAspect {

    @AfterReturning(
        pointcut = "execution(* com.internship.tool.service.*.create*(..)) || " +
                   "execution(* com.internship.tool.service.*.update*(..)) || " +
                   "execution(* com.internship.tool.service.*.delete*(..))",
        returning = "result"
    )
    public void logAudit(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        System.out.println("[AUDIT LOG] " + LocalDateTime.now() +
            " | Method: " + methodName +
            " | Class: " + className);
    }
}