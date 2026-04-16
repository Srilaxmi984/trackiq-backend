package com.trackiq.backend.service;

import com.trackiq.backend.entity.Issue;
import com.trackiq.backend.entity.Project;
import com.trackiq.backend.entity.User;
import com.trackiq.backend.enums.IssueStatus;
import com.trackiq.backend.repository.IssueRepository;
import com.trackiq.backend.repository.ProjectRepository;
import com.trackiq.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class IssueService {

    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final EmailService emailService; // 🔥 NEW



    // 🔥 UPDATED CONSTRUCTOR
    public IssueService(IssueRepository issueRepository,
                        UserRepository userRepository,
                        ProjectRepository projectRepository,
                        EmailService emailService) {
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.emailService = emailService;

    }

    // 🔹 Create Issue (Reporter)
    public Issue createIssue(Issue issue, Long reporterId, Long projectId) {

        User reporter = userRepository.findById(reporterId).orElseThrow();
        Project project = projectRepository.findById(projectId).orElseThrow();

        issue.setReporter(reporter);
        issue.setProject(project);
        issue.setStatus(IssueStatus.SUBMITTED);

        return issueRepository.save(issue);
    }

    // 🔹 Assign Issue (Manager)
    public Issue assignIssue(Long issueId, Long developerId) {

        Issue issue = issueRepository.findById(issueId).orElseThrow();
        User developer = userRepository.findById(developerId).orElseThrow();

        issue.setAssignedTo(developer);
        issue.setStatus(IssueStatus.ASSIGNED);
        issue.setUpdatedAt(LocalDateTime.now());
        // 🔥 SEND EMAIL TO DEVELOPER
        emailService.sendEmail(
                developer.getEmail(),
                "Issue Assigned",
                "You have been assigned: " + issue.getTitle()
        );

        return issueRepository.save(issue);
    }

    // 🔹 Update Status
    public Issue updateStatus(Long issueId, IssueStatus newStatus) {

        // 🔹 1. GET ISSUE
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("❌ Issue not found"));

        // 🔴 2. CHECK IF ASSIGNED
        if (issue.getAssignedTo() == null) {
            throw new RuntimeException("❌ Assign issue to a developer first");
        }

        // 🔹 3. CURRENT STATUS
        IssueStatus currentStatus = issue.getStatus();

        // 🔴 4. WORKFLOW VALIDATION

        if (newStatus == IssueStatus.IN_PROGRESS && currentStatus != IssueStatus.ASSIGNED) {
            throw new RuntimeException("❌ Must be ASSIGNED before moving to IN_PROGRESS");
        }

        if (newStatus == IssueStatus.TESTING && currentStatus != IssueStatus.IN_PROGRESS) {
            throw new RuntimeException("❌ Must be IN_PROGRESS before moving to TESTING");
        }

        if (newStatus == IssueStatus.DONE && currentStatus != IssueStatus.TESTING) {
            throw new RuntimeException("❌ Must be TESTING before moving to DONE");
        }

        // 🔹 5. UPDATE STATUS
        issue.setStatus(newStatus);

        // 🔥 6. SEND EMAIL WHEN ISSUE IS DONE
        if (newStatus == IssueStatus.DONE) {
            try {
                emailService.sendEmail(
                        issue.getReporter().getEmail(),
                        "✅ Issue Completed",
                        "Hello,\n\nYour issue has been resolved successfully.\n\n"
                                + "Issue Title: " + issue.getTitle() + "\n"
                                + "Project: " + (issue.getProject() != null ? issue.getProject().getName() : "-") + "\n\n"
                                + "Thank you for using TrackIQ 🚀"
                );
            } catch (Exception e) {
                // 🔴 IMPORTANT: Do not break flow if email fails
                System.out.println("⚠️ Email failed: " + e.getMessage());
            }
        }

        // 🔹 7. SAVE
        return issueRepository.save(issue);
    }
    // 🔹 Feedback
    public Issue giveFeedback(Long issueId, String feedback) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        if (!issue.getStatus().name().equals("DONE")) {
            throw new RuntimeException("Issue not completed yet");
        }

        if (issue.isFeedbackGiven()) {
            throw new RuntimeException("Feedback already given");
        }

        issue.setFeedback(feedback);
        issue.setFeedbackGiven(true);

        // 🔥 SEND EMAIL TO MANAGER
        emailService.sendEmail(
                issue.getProject().getCreatedBy().getEmail(),
                "Feedback Received",
                "Feedback: " + feedback
        );

        return issueRepository.save(issue);
    }
    public List<Issue> getIssuesByDeveloper(String email) {
        return issueRepository.findByAssignedToEmail(email);
    }

    // 🔹 Manager Dashboard
    public Map<String, Long> getManagerDashboard() {

        List<Issue> issues = issueRepository.findAll();

        Map<String, Long> stats = new HashMap<>();

        stats.put("totalIssues", (long) issues.size());
        stats.put("submitted", issues.stream().filter(i -> i.getStatus().name().equals("SUBMITTED")).count());
        stats.put("assigned", issues.stream().filter(i -> i.getStatus().name().equals("ASSIGNED")).count());
        stats.put("inProgress", issues.stream().filter(i -> i.getStatus().name().equals("IN_PROGRESS")).count());
        stats.put("done", issues.stream().filter(i -> i.getStatus().name().equals("DONE")).count());

        return stats;
    }
    public Issue getIssueById(Long id) {
        return issueRepository.findById(id).orElseThrow();
    }

    public Issue save(Issue issue) {
        return issueRepository.save(issue);
    }

    // 🔹 Reporter Dashboard
    public Map<String, Long> getReporterDashboard(Long reporterId) {

        List<Issue> issues = issueRepository.findByReporterId(reporterId);

        Map<String, Long> stats = new HashMap<>();

        stats.put("myComplaints", (long) issues.size());
        stats.put("open", issues.stream().filter(i -> !i.getStatus().name().equals("DONE")).count());
        stats.put("resolved", issues.stream().filter(i -> i.getStatus().name().equals("DONE")).count());

        return stats;
    }

    // 🔹 Developer Dashboard
    public Map<String, Long> getDeveloperDashboard(Long devId) {

        List<Issue> issues = issueRepository.findByAssignedToId(devId);

        Map<String, Long> stats = new HashMap<>();

        stats.put("assigned", issues.stream().filter(i -> i.getStatus().name().equals("ASSIGNED")).count());
        stats.put("inProgress", issues.stream().filter(i -> i.getStatus().name().equals("IN_PROGRESS")).count());
        stats.put("completed", issues.stream().filter(i -> i.getStatus().name().equals("DONE")).count());

        return stats;
    }

    // 🔹 Get All Issues
    public List<Issue> getAllIssues() {
        return issueRepository.findAll();
    }

    // 🔹 Reporter Issues
    public List<Issue> getReporterIssues(Long reporterId) {
        return issueRepository.findByReporterId(reporterId);
    }

    // 🔹 Developer Issues
    public List<Issue> getDeveloperIssues(Long developerId) {
        return issueRepository.findByAssignedToId(developerId);
    }


    // 🔹 Kanban
    public Map<String, List<Issue>> getKanban(Long projectId) {

        List<Issue> issues = issueRepository.findByProjectId(projectId);

        Map<String, List<Issue>> kanban = new HashMap<>();

        kanban.put("SUBMITTED", new ArrayList<>());
        kanban.put("ASSIGNED", new ArrayList<>());
        kanban.put("IN_PROGRESS", new ArrayList<>());
        kanban.put("DONE", new ArrayList<>());

        for (Issue issue : issues) {
            kanban.get(issue.getStatus().name()).add(issue);
        }

        return kanban;
    }
    public Issue getById(Long id) {
        return issueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue not found"));
    }
}