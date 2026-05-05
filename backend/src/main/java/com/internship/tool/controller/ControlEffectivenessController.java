package com.internship.tool.controller;

import com.internship.tool.dto.ControlEffectivenessRequest;
import com.internship.tool.dto.ControlEffectivenessResponse;
import com.internship.tool.dto.StatsResponse;
import com.internship.tool.service.ControlEffectivenessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/controls")
@Tag(name = "Control Effectiveness", description = "CRUD and AI operations for control effectiveness ratings")
@CrossOrigin(origins = "*")
public class ControlEffectivenessController {

    private final ControlEffectivenessService service;

    public ControlEffectivenessController(ControlEffectivenessService service) {
        this.service = service;
    }

    @GetMapping("/all")
    @Operation(summary = "Get all controls (paginated)")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<ControlEffectivenessResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(service.getAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get control by ID")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ControlEffectivenessResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping("/create")
    @Operation(summary = "Create a new control")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ControlEffectivenessResponse> create(
            @Valid @RequestBody ControlEffectivenessRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing control")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ControlEffectivenessResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ControlEffectivenessRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft-delete a control")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        service.softDelete(id);
        return ResponseEntity.ok(Map.of("message", "Control deleted successfully"));
    }

    @GetMapping("/search")
    @Operation(summary = "Search controls by query string")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<ControlEffectivenessResponse>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(service.search(q, pageable));
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter controls by status, category, risk level, and date range")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<ControlEffectivenessResponse>> filter(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String riskLevel,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(service.filter(status, category, riskLevel, startDate, endDate, pageable));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get dashboard statistics")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<StatsResponse> getStats() {
        return ResponseEntity.ok(service.getStats());
    }

    @PostMapping("/{id}/ai/recommend")
    @Operation(summary = "Trigger AI recommendations for a control")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ControlEffectivenessResponse> aiRecommend(@PathVariable Long id) {
        return ResponseEntity.ok(service.triggerAiRecommendation(id));
    }

    @PostMapping("/{id}/ai/report")
    @Operation(summary = "Generate AI report for a control")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ControlEffectivenessResponse> aiReport(@PathVariable Long id) {
        return ResponseEntity.ok(service.triggerAiReport(id));
    }

    @GetMapping("/export")
    @Operation(summary = "Export all controls as CSV")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public void exportCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=controls_export.csv");

        List<ControlEffectivenessResponse> controls = service.getAllForExport();

        PrintWriter writer = response.getWriter();
        writer.println("ID,Control Name,Category,Risk Level,Score,Status,Assessor,Department,Review Date,Created At");
        for (ControlEffectivenessResponse c : controls) {
            writer.printf("%d,\"%s\",\"%s\",\"%s\",%s,\"%s\",\"%s\",\"%s\",%s,%s%n",
                    c.getId(),
                    escapeCsv(c.getControlName()),
                    escapeCsv(c.getCategory()),
                    c.getRiskLevel(),
                    c.getEffectivenessScore() != null ? c.getEffectivenessScore() : "",
                    c.getStatus(),
                    escapeCsv(c.getAssessor()),
                    escapeCsv(c.getDepartment()),
                    c.getReviewDate() != null ? c.getReviewDate() : "",
                    c.getCreatedAt()
            );
        }
        writer.flush();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        return value.replace("\"", "\"\"");
    }
}
