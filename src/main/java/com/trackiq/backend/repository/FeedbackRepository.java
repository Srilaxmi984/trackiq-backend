package com.trackiq.backend.repository;

import com.trackiq.backend.entity.Feedback;
import com.trackiq.backend.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    Optional<Feedback> findByIssue(Issue issue);
}