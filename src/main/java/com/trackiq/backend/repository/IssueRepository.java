package com.trackiq.backend.repository;

import com.trackiq.backend.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {

    List<Issue> findByReporterId(Long reporterId);

    List<Issue> findByAssignedToId(Long developerId);

    List<Issue> findByProjectId(Long projectId);

    List<Issue> findByAssignedToEmail(String email);


}