package com.example.civic_connect.repository;

import com.example.civic_connect.model.IssueUpdate;
import com.example.civic_connect.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository; // No longer needed
import java.util.List;

// @Repository annotation is not needed
public interface IssueUpdateRepository extends JpaRepository<IssueUpdate, Long> {
    List<IssueUpdate> findByIssueOrderByCreatedAtAsc(Issue issue);
}