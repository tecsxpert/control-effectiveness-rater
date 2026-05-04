package com.internship.tool.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Configuration
public class DataSeeder implements CommandLineRunner {

    private final DataSource dataSource;

    public DataSeeder(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            // Create table if not exists
            conn.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS control_effectiveness (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "control_name VARCHAR(255)," +
                "control_description TEXT," +
                "category VARCHAR(100)," +
                "status VARCHAR(50) DEFAULT 'PENDING'," +
                "effectiveness_score INTEGER," +
                "risk_level VARCHAR(50)," +
                "owner VARCHAR(255)," +
                "ai_description TEXT," +
                "ai_recommendations TEXT," +
                "is_deleted BOOLEAN DEFAULT FALSE," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"
            );

            ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM control_effectiveness");
            rs.next();
            if (rs.getInt(1) > 0) return;

            String sql = "INSERT INTO control_effectiveness (control_name, control_description, category, status, effectiveness_score, risk_level, owner) VALUES (?, ?, ?, ?, ?, ?, ?)";
            Object[][] records = {
                {"Access Control Policy", "Controls user access to systems", "Security", "ACTIVE", 85, "LOW", "Alice Johnson"},
                {"Data Encryption", "Encrypts sensitive data at rest", "Security", "ACTIVE", 90, "LOW", "Bob Smith"},
                {"Firewall Configuration", "Network perimeter protection", "Network", "ACTIVE", 75, "MEDIUM", "Carol White"},
                {"Password Policy", "Enforces strong password rules", "Security", "PENDING", 60, "MEDIUM", "David Brown"},
                {"Backup and Recovery", "Daily automated backups", "Operations", "ACTIVE", 80, "LOW", "Eve Davis"},
                {"Incident Response", "Handles security incidents", "Security", "REVIEW", 55, "HIGH", "Frank Miller"},
                {"Vulnerability Scanning", "Weekly automated scans", "Security", "ACTIVE", 70, "MEDIUM", "Grace Wilson"},
                {"Change Management", "Controls system changes", "Operations", "PENDING", 45, "HIGH", "Henry Moore"},
                {"User Awareness Training", "Annual security awareness training", "HR", "ACTIVE", 65, "MEDIUM", "Ivy Taylor"},
                {"Physical Security", "Controls physical access", "Facilities", "ACTIVE", 88, "LOW", "Jack Anderson"},
                {"Audit Logging", "Logs all system activities", "Compliance", "ACTIVE", 92, "LOW", "Karen Thomas"},
                {"Patch Management", "Monthly patch updates", "Operations", "PENDING", 50, "HIGH", "Leo Jackson"},
                {"Business Continuity Plan", "Ensures business continuity", "Operations", "REVIEW", 40, "CRITICAL", "Mia White"},
                {"Third Party Risk", "Manages vendor risks", "Compliance", "PENDING", 35, "CRITICAL", "Noah Harris"},
                {"Data Classification", "Classifies sensitive data", "Compliance", "ACTIVE", 78, "MEDIUM", "Olivia Martin"}
            };

            PreparedStatement ps = conn.prepareStatement(sql);
            for (Object[] record : records) {
                ps.setString(1, (String) record[0]);
                ps.setString(2, (String) record[1]);
                ps.setString(3, (String) record[2]);
                ps.setString(4, (String) record[3]);
                ps.setInt(5, (Integer) record[4]);
                ps.setString(6, (String) record[5]);
                ps.setString(7, (String) record[6]);
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("✅ Data seeder: 15 records inserted successfully!");
        } catch (Exception e) {
            System.out.println("⚠️ Seeder error: " + e.getMessage());
        }
    }
}
