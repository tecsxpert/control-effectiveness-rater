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

        // Seed 30 realistic control effectiveness records
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
            build("Cloud Security Posture", "AWS/Azure security configuration audit using CIS Benchmark framework", "Cloud Security", "CRITICAL", 39, "PENDING", "Sophia Clark", "Cloud Ops", LocalDate.of(2026, 5, 20), user),
            build("Multi-Factor Authentication", "Verification that MFA is enforced on all privileged accounts, VPN access, and SaaS applications", "Identity Management", "CRITICAL", 89, "COMPLETED", "Nancy Wright", "IT Security", LocalDate.of(2026, 2, 15), admin),
            build("Security Patch Management", "Monthly patching cycle assessment for all servers, workstations, and network devices", "Vulnerability Management", "HIGH", 63, "IN_PROGRESS", "Oscar Hill", "IT Operations", LocalDate.of(2026, 4, 30), user),
            build("Data Loss Prevention", "DLP policy configuration and enforcement effectiveness across email, web, and endpoint channels", "Data Protection", "HIGH", 47, "PENDING", "Patricia Green", "Data Governance", LocalDate.of(2026, 5, 25), admin),
            build("API Gateway Security", "Rate limiting, authentication, and input validation controls on all externally exposed APIs", "Application Security", "MEDIUM", 77, "COMPLETED", "Quentin Adams", "Development", LocalDate.of(2026, 3, 5), user),
            build("Zero Trust Network Access", "Micro-segmentation and identity-based access controls replacing legacy VPN infrastructure", "Network Security", "CRITICAL", 58, "IN_PROGRESS", "Rebecca Scott", "Infrastructure", LocalDate.of(2026, 4, 28), admin),
            build("Privileged Access Management", "Vaulting, session recording, and just-in-time access for all privileged administrator accounts", "Access Management", "CRITICAL", 82, "REVIEWED", "Samuel Baker", "IT Security", LocalDate.of(2026, 3, 30), user),
            build("Container Security Scanning", "Image vulnerability scanning and runtime security policy enforcement for all Docker containers", "DevSecOps", "HIGH", 44, "PENDING", "Tina Carter", "DevOps", LocalDate.of(2026, 5, 15), admin),
            build("Email Security Controls", "SPF, DKIM, DMARC configuration and anti-phishing filter effectiveness across all mail domains", "Email Security", "MEDIUM", 91, "COMPLETED", "Uma Patel", "IT Security", LocalDate.of(2026, 2, 20), user),
            build("Cryptographic Key Management", "Secure generation, storage, rotation, and destruction lifecycle for all cryptographic keys", "Data Protection", "HIGH", 53, "PENDING", "Victor Hughes", "Cloud Ops", LocalDate.of(2026, 5, 30), admin),
            build("Mobile Device Management", "Enforcement of MDM policies covering encryption, remote wipe, and app control for corporate and BYOD devices", "Endpoint Security", "MEDIUM", 86, "COMPLETED", "Wendy Morgan", "IT Support", LocalDate.of(2026, 3, 12), user),
            build("Software Supply Chain Security", "SBOM tracking and third-party dependency vulnerability monitoring for all production applications", "Third Party Risk", "HIGH", 31, "PENDING", "Xavier Reed", "Development", LocalDate.of(2026, 5, 20), admin),
            build("Threat Intelligence Integration", "Operationalizing external threat feeds into SIEM alerting rules and firewall blocklists", "Monitoring", "HIGH", 65, "IN_PROGRESS", "Yvonne Collins", "SOC", LocalDate.of(2026, 4, 22), user),
            build("Database Activity Monitoring", "Real-time monitoring of privileged database queries and anomaly detection for sensitive tables", "Monitoring", "MEDIUM", 78, "REVIEWED", "Zachary Mitchell", "Database Team", LocalDate.of(2026, 3, 28), admin),
            build("Identity Lifecycle Management", "Automated provisioning and de-provisioning of accounts tied to HR system integration", "Identity Management", "HIGH", 69, "IN_PROGRESS", "Alice Cooper", "HR & IT", LocalDate.of(2026, 4, 10), user),
            build("Security Configuration Baseline", "CIS Benchmark compliance assessment for all servers, databases, and cloud resource configurations", "Configuration Management", "MEDIUM", 72, "COMPLETED", "Bob Stewart", "IT Compliance", LocalDate.of(2026, 3, 22), admin)
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
