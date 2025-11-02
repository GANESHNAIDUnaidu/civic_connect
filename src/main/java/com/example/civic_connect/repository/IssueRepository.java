package com.example.civic_connect.repository;

import com.example.civic_connect.model.Issue;
import com.example.civic_connect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository; // No longer needed

import java.util.List;

// @Repository annotation is redundant for JpaRepository interfaces
public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findByCitizen(User citizen);
    List<Issue> findByAssignedAdmin(User assignedAdmin);
    List<Issue> findByStatus(String status);
    List<Issue> findByCitizenAndStatus(User citizen, String status);
    List<Issue> findByCategory(String category);
}