package com.internship.tool.config;

import com.internship.tool.entity.ControlEffectiveness;
import com.internship.tool.entity.User;
import com.internship.tool.repository.ControlEffectivenessRepository;
import com.internship.tool.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final ControlEffectivenessRepository controlRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      ControlEffectivenessRepository controlRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.controlRepository = controlRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Data already seeded — skipping.");
            return;
        }

        log.info("Seeding demo data...");

        // Seed users
        User admin = userRepository.save(User.builder()
                .username("admin")
                .email("admin@tool124.com")
                .password(passwordEncoder.encode("admin123"))
                .role("ADMIN")
                .build());

        User user = userRepository.save(User.builder()
                .username("analyst")
                .email("analyst@tool124.com")
                .password(passwordEncoder.encode("analyst123"))
                .role("USER")
                .build());

        log.info("Seeded 2 users: admin / analyst");

        // Seed 15 realistic control effectiveness records
        List<ControlEffectiveness> controls = List.of(
            build("Firewall Rule Review", "Quarterly review of firewall rules to ensure no unauthorized access paths exist", "Network Security", "HIGH", 78, "COMPLETED", "Sarah Chen", "IT Security", LocalDate.of(2026, 3, 15), admin),
            build("Access Control Policy", "Annual review of role-based access control policies across all critical systems", "Access Management", "CRITICAL", 45, "IN_PROGRESS", "James Wilson", "Compliance", LocalDate.of(2026, 4, 30), admin),
            build("Data Encryption at Rest", "Verification that AES-256 encryption is applied to all databases storing PII", "Data Protection", "CRITICAL", 92, "COMPLETED", "Maria Garcia", "Data Engineering", LocalDate.of(2026, 2, 28), user),
            build("Incident Response Plan", "Bi-annual test of the incident response plan with tabletop exercises", "Incident Management", "HIGH", 67, "REVIEWED", "David Kim", "Security Ops", LocalDate.of(2026, 5, 1), admin),
            build("Password Policy Enforcement", "Check that password complexity, rotation, and MFA requirements are enforced", "Access Management", "MEDIUM", 88, "COMPLETED", "Lisa Brown", "IT Security", LocalDate.of(2026, 3, 20), user),
            build("Vendor Risk Assessment", "Third-party vendor security questionnaire and compliance verification process", "Third Party Risk", "HIGH", 52, "PENDING", "Robert Taylor", "Procurement", LocalDate.of(2026, 5, 15), admin),
            build("Network Segmentation", "Verify micro-segmentation controls between production and development environments", "Network Security", "CRITICAL", 71, "IN_PROGRESS", "Emily Davis", "Infrastructure", LocalDate.of(2026, 4, 25), user),
            build("Security Awareness Training", "Completion rate and phishing simulation results for all employees", "Human Resources", "MEDIUM", 83, "COMPLETED", "Michael Johnson", "HR", LocalDate.of(2026, 3, 10), admin),
            build("Backup and Recovery Test", "Monthly backup integrity verification and disaster recovery drill results", "Business Continuity", "HIGH", 76, "REVIEWED", "Anna Martinez", "IT Ops", LocalDate.of(2026, 4, 5), user),
            build("Application Code Review", "Static and dynamic analysis of critical web applications for OWASP Top 10 vulnerabilities", "Application Security", "HIGH", 60, "IN_PROGRESS", "Chris Lee", "Development", LocalDate.of(2026, 4, 20), admin),
            build("Physical Access Controls", "Badge reader audit and CCTV coverage assessment for data center facilities", "Physical Security", "LOW", 95, "COMPLETED", "Jennifer White", "Facilities", LocalDate.of(2026, 1, 30), user),
            build("Log Monitoring and SIEM", "Evaluate SIEM rule coverage, alert accuracy, and mean time to detect anomalies", "Monitoring", "HIGH", 55, "PENDING", "Daniel Anderson", "SOC", LocalDate.of(2026, 5, 10), admin),
            build("Change Management Process", "Assessment of change advisory board procedures and emergency change controls", "Governance", "MEDIUM", 81, "COMPLETED", "Rachel Thomas", "IT Governance", LocalDate.of(2026, 3, 25), user),
            build("Endpoint Protection", "Antivirus coverage, EDR deployment status, and patch compliance across all endpoints", "Endpoint Security", "MEDIUM", 74, "IN_PROGRESS", "Kevin Robinson", "IT Support", LocalDate.of(2026, 4, 15), admin),
            build("Cloud Security Posture", "AWS/Azure security configuration audit using CIS Benchmark framework", "Cloud Security", "CRITICAL", 39, "PENDING", "Sophia Clark", "Cloud Ops", LocalDate.of(2026, 5, 20), user)
        );

        controlRepository.saveAll(controls);
        log.info("Seeded {} control effectiveness records.", controls.size());
    }

    private ControlEffectiveness build(String name, String desc, String category,
                                        String riskLevel, int score, String status,
                                        String assessor, String department,
                                        LocalDate reviewDate, User createdBy) {
        return ControlEffectiveness.builder()
                .controlName(name)
                .controlDescription(desc)
                .category(category)
                .riskLevel(riskLevel)
                .effectivenessScore(score)
                .status(status)
                .assessor(assessor)
                .department(department)
                .reviewDate(reviewDate)
                .createdBy(createdBy)
                .build();
    }
}
