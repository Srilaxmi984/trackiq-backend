package com.trackiq.backend.controller;

import com.trackiq.backend.entity.Issue;
import com.trackiq.backend.enums.IssueStatus;
import com.trackiq.backend.service.IssueService;
import lombok.Data;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.Map;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.io.IOException;
@RestController
@RequestMapping("/api/issues")
@CrossOrigin
@Data
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    // 🔹 Create Issue → REPORTER ONLY
    @PreAuthorize("hasRole('REPORTER')")
    @PostMapping("/{reporterId}/{projectId}")
    public Issue createIssue(@RequestBody Issue issue,
                             @PathVariable Long reporterId,
                             @PathVariable Long projectId) {
        return issueService.createIssue(issue, reporterId, projectId);
    }

    // 🔹 Assign Issue → MANAGER ONLY (OLD VERSION - KEEP)
    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/assign/{issueId}/{developerId}")
    public Issue assignIssueOld(@PathVariable Long issueId,
                                @PathVariable Long developerId) {
        return issueService.assignIssue(issueId, developerId);
    }

    // 🔹 Update Status → DEVELOPER ONLY
    @PreAuthorize("hasRole('DEVELOPER')")
    @PutMapping("/status/{issueId}")
    public Issue updateStatus(@PathVariable Long issueId,
                              @RequestParam IssueStatus status) {
        return issueService.updateStatus(issueId, status);
    }

    // 🔹 Get All Issues → MANAGER + DEVELOPER
    @PreAuthorize("hasAnyRole('MANAGER','DEVELOPER')")
    @GetMapping
    public List<Issue> getAllIssues() {
        return issueService.getAllIssues();
    }

    // 🔹 Assign Issue → MANAGER ONLY (NEW VERSION - JSON BODY)
    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('MANAGER')")
    public Issue assignIssue(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        Long devId = body.get("developerId");
        return issueService.assignIssue(id, devId);
    }

    @PreAuthorize("hasAnyRole('MANAGER','DEVELOPER')")
    @GetMapping("/kanban/{projectId}")
    public Map<String, List<Issue>> getKanban(@PathVariable Long projectId) {
        return issueService.getKanban(projectId);
    }

    // 🔹 Reporter Issues → REPORTER ONLY
    @PreAuthorize("hasRole('REPORTER')")
    @GetMapping("/reporter/{id}")
    public List<Issue> getReporterIssues(@PathVariable Long id) {
        return issueService.getReporterIssues(id);
    }

    // 🔹 Developer Issues → DEVELOPER ONLY
    @PreAuthorize("hasRole('DEVELOPER')")
    @GetMapping("/developer/{id}")
    public List<Issue> getDeveloperIssues(@PathVariable Long id) {
        return issueService.getDeveloperIssues(id);
    }

    // 🔹 Manager Dashboard
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/dashboard/manager")
    public Map<String, Long> getManagerDashboard() {
        return issueService.getManagerDashboard();
    }

    // 🔹 Reporter Dashboard
    @PreAuthorize("hasRole('REPORTER')")
    @GetMapping("/dashboard/reporter/{id}")
    public Map<String, Long> getReporterDashboard(@PathVariable Long id) {
        return issueService.getReporterDashboard(id);
    }

    // 🔹 Developer Dashboard
    @PreAuthorize("hasRole('DEVELOPER')")
    @GetMapping("/dashboard/developer/{id}")
    public Map< String, Long> getDeveloperDashboard(@PathVariable Long id) {
        return issueService.getDeveloperDashboard(id);
    }
    // 🔥 UPDATE ISSUE STATUS (KANBAN DRAG)
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('MANAGER','DEVELOPER')")
    public Issue updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        String status = body.get("status");

        return issueService.updateStatus(id, IssueStatus.valueOf(status));
    }
    @GetMapping("/my")
    public List<Issue> getMyIssues(Authentication auth) {
        String email = auth.getName();
        return issueService.getIssuesByDeveloper(email);
    }
    @PreAuthorize("hasRole('DEVELOPER')")
    @PostMapping(value = "/resolve/{issueId}", consumes = "multipart/form-data")
    public Issue resolveIssue(
            @PathVariable Long issueId,
            @RequestParam String notes,
            @RequestParam(required = false) MultipartFile file
    ) throws Exception {

        Issue issue = issueService.getById(issueId);

        issue.setResolutionNotes(notes);
        issue.setStatus(IssueStatus.DONE); // or TESTING if needed

        if (file != null && !file.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String filePath = uploadDir + fileName;
            file.transferTo(new File(filePath));

            issue.setResolutionFileUrl("/uploads/" + fileName);
        }

        return issueService.save(issue);
    }

    // 🔹 Upload File → REPORTER
    @PostMapping(value = "/{issueId}/upload", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('REPORTER')")
    public Issue uploadFile(
            @PathVariable Long issueId,
            @RequestParam("file") MultipartFile file
    ) throws Exception {

        Issue issue = issueService.getIssueById(issueId);

        if (file != null && !file.isEmpty()) {

            String cleanName = file.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
            String fileName = System.currentTimeMillis() + "_" + cleanName;

            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String filePath = uploadDir + fileName;
            file.transferTo(new File(filePath));

            issue.setFileUrl("/uploads/" + fileName);
            return issueService.save(issue);
        }

        return issue;
    }
    @PutMapping("/feedback/{issueId}")
    public Issue giveFeedback(
            @PathVariable Long issueId,
            @RequestParam String feedback,
            @RequestParam int rating   // ✅ ADD THIS
    ) {
        Issue issue = issueService.getById(issueId);

        issue.setFeedback(feedback);
        issue.setRating(rating);
        issue.setFeedbackGiven(true);

        return issueService.save(issue);
    }
}