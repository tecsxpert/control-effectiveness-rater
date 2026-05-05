package com.internship.tool.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@RestController
@RequestMapping("/api/controls")
@CrossOrigin(origins = "http://localhost:5173")
public class ControlController {

    private final DataSource dataSource;

    public ControlController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() throws Exception {
        Map<String, Object> stats = new HashMap<>();
        try (Connection conn = dataSource.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT COUNT(*) as total, " +
                "SUM(CASE WHEN status='ACTIVE' THEN 1 ELSE 0 END) as active, " +
                "SUM(CASE WHEN status='PENDING' THEN 1 ELSE 0 END) as pending, " +
                "SUM(CASE WHEN status='INACTIVE' THEN 1 ELSE 0 END) as inactive " +
                "FROM control_effectiveness WHERE is_deleted=FALSE"
            );
            if (rs.next()) {
                stats.put("total", rs.getInt("total"));
                stats.put("active", rs.getInt("active"));
                stats.put("pending", rs.getInt("pending"));
                stats.put("inactive", rs.getInt("inactive"));
            }
        }
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Map<String, Object>>> getAll() throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT * FROM control_effectiveness WHERE is_deleted=FALSE ORDER BY id"
            );
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getLong("id"));
                row.put("controlName", rs.getString("control_name"));
                row.put("controlDescription", rs.getString("control_description"));
                row.put("category", rs.getString("category"));
                row.put("status", rs.getString("status"));
                row.put("effectivenessScore", rs.getInt("effectiveness_score"));
                row.put("riskLevel", rs.getString("risk_level"));
                row.put("owner", rs.getString("owner"));
                list.add(row);
            }
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM control_effectiveness WHERE id=? AND is_deleted=FALSE"
            );
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getLong("id"));
                row.put("controlName", rs.getString("control_name"));
                row.put("controlDescription", rs.getString("control_description"));
                row.put("category", rs.getString("category"));
                row.put("status", rs.getString("status"));
                row.put("effectivenessScore", rs.getInt("effectiveness_score"));
                row.put("riskLevel", rs.getString("risk_level"));
                row.put("owner", rs.getString("owner"));
                return ResponseEntity.ok(row);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> body) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO control_effectiveness (control_name, control_description, category, status, effectiveness_score, risk_level, owner) VALUES (?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, (String) body.get("controlName"));
            ps.setString(2, (String) body.get("controlDescription"));
            ps.setString(3, (String) body.get("category"));
            ps.setString(4, (String) body.getOrDefault("status", "PENDING"));
            ps.setInt(5, Integer.parseInt(body.getOrDefault("effectivenessScore", "0").toString()));
            ps.setString(6, (String) body.get("riskLevel"));
            ps.setString(7, (String) body.get("owner"));
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                body.put("id", keys.getLong(1));
            }
        }
        return ResponseEntity.ok(body);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody Map<String, Object> body) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE control_effectiveness SET control_name=?, control_description=?, category=?, status=?, effectiveness_score=?, risk_level=?, owner=? WHERE id=?"
            );
            ps.setString(1, (String) body.get("controlName"));
            ps.setString(2, (String) body.get("controlDescription"));
            ps.setString(3, (String) body.get("category"));
            ps.setString(4, (String) body.get("status"));
            ps.setInt(5, Integer.parseInt(body.getOrDefault("effectivenessScore", "0").toString()));
            ps.setString(6, (String) body.get("riskLevel"));
            ps.setString(7, (String) body.get("owner"));
            ps.setLong(8, id);
            ps.executeUpdate();
        }
        body.put("id", id);
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE control_effectiveness SET is_deleted=TRUE WHERE id=?"
            );
            ps.setLong(1, id);
            ps.executeUpdate();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> search(@RequestParam String q) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM control_effectiveness WHERE is_deleted=FALSE AND (LOWER(control_name) LIKE ? OR LOWER(category) LIKE ? OR LOWER(owner) LIKE ?)"
            );
            String query = "%" + q.toLowerCase() + "%";
            ps.setString(1, query);
            ps.setString(2, query);
            ps.setString(3, query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getLong("id"));
                row.put("controlName", rs.getString("control_name"));
                row.put("category", rs.getString("category"));
                row.put("status", rs.getString("status"));
                row.put("effectivenessScore", rs.getInt("effectiveness_score"));
                row.put("riskLevel", rs.getString("risk_level"));
                row.put("owner", rs.getString("owner"));
                list.add(row);
            }
        }
        return ResponseEntity.ok(list);
    }
}
