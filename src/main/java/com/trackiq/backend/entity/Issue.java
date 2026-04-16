package com.trackiq.backend.entity;

import com.trackiq.backend.enums.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.LocalDateTime;
@Entity
public class Issue {
    private String feedback;
    private boolean feedbackGiven = false;
    @ManyToOne
    private User assignedTo;
    private String resolutionNotes;
    private String resolutionFileUrl;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    private Integer rating;

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
    public String getResolutionNotes() {
        return resolutionNotes;
    }

    public void setResolutionNotes(String resolutionNotes) {
        this.resolutionNotes = resolutionNotes;
    }

    public String getResolutionFileUrl() {
        return resolutionFileUrl;
    }

    public void setResolutionFileUrl(String resolutionFileUrl) {
        this.resolutionFileUrl = resolutionFileUrl;
    }
    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public boolean isFeedbackGiven() {
        return feedbackGiven;
    }

    public void setFeedbackGiven(boolean feedbackGiven) {
        this.feedbackGiven = feedbackGiven;
    }
    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    private IssueStatus status;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String stepsToReproduce;

    private String impact;

    private String fileUrl;

    private LocalDateTime createdAt;


    // Relationships

    @ManyToOne
    private Project project;

    @ManyToOne
    private User reporter;



    // ===== Auto Timestamp =====

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ===== Getters & Setters =====
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public User getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
    }
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }



}