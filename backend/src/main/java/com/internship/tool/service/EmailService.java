package com.internship.tool.service;

import com.internship.tool.entity.ControlEffectiveness;
import com.internship.tool.repository.ControlEffectivenessRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final ControlEffectivenessRepository repository;

    @Value("${app.mail.from}")
    private String fromAddress;

    public EmailService(JavaMailSender mailSender,
                        TemplateEngine templateEngine,
                        ControlEffectivenessRepository repository) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.repository = repository;
    }

    public void sendNotification(String to, String subject, String controlName,
                                  String action, String details) {
        try {
            Context context = new Context();
            context.setVariable("controlName", controlName);
            context.setVariable("action", action);
            context.setVariable("details", details);
            context.setVariable("date", LocalDate.now().toString());

            String htmlContent = templateEngine.process("notification", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Notification email sent to {} for control: {}", to, controlName);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    /**
     * Daily reminder — runs every day at 8:00 AM.
     * Finds controls with review dates in the past that are still PENDING or IN_PROGRESS.
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void sendDailyReminders() {
        log.info("Running daily reminder check...");
        List<ControlEffectiveness> overdue = repository
                .findByIsDeletedFalseAndReviewDateBefore(LocalDate.now());

        for (ControlEffectiveness control : overdue) {
            if ("PENDING".equals(control.getStatus()) || "IN_PROGRESS".equals(control.getStatus())) {
                if (control.getCreatedBy() != null && control.getCreatedBy().getEmail() != null) {
                    sendNotification(
                            control.getCreatedBy().getEmail(),
                            "Overdue Review: " + control.getControlName(),
                            control.getControlName(),
                            "OVERDUE_REMINDER",
                            "This control's review date was " + control.getReviewDate() +
                            " and it is still in " + control.getStatus() + " status."
                    );
                }
            }
        }
        log.info("Daily reminder check completed. Found {} overdue controls.", overdue.size());
    }

    /**
     * Deadline alert — runs every day at 6:00 PM.
     * Alerts for controls with review dates tomorrow.
     */
    @Scheduled(cron = "0 0 18 * * *")
    public void sendDeadlineAlerts() {
        log.info("Running deadline alert check...");
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<ControlEffectiveness> upcoming = repository
                .findByIsDeletedFalseAndReviewDateBefore(tomorrow.plusDays(1));

        for (ControlEffectiveness control : upcoming) {
            if (tomorrow.equals(control.getReviewDate())) {
                if (control.getCreatedBy() != null && control.getCreatedBy().getEmail() != null) {
                    sendNotification(
                            control.getCreatedBy().getEmail(),
                            "Deadline Tomorrow: " + control.getControlName(),
                            control.getControlName(),
                            "DEADLINE_ALERT",
                            "The review deadline for this control is tomorrow (" + tomorrow + ")."
                    );
                }
            }
        }
        log.info("Deadline alert check completed.");
    }
}
