package com.example.civic_connect.service;

import com.example.civic_connect.model.Issue;
import com.example.civic_connect.model.User;
import com.example.civic_connect.repository.IssueRepository;
// import org.springframework.beans.factory.annotation.Autowired; // No longer needed
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException; // Import this

@Service
public class IssueService {

    private final IssueRepository issueRepository;

    // Use constructor injection
    public IssueService(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    public List<Issue> getAllIssues() {
        return issueRepository.findAll();
    }

    public List<Issue> getIssuesByCitizen(User citizen) {
        return issueRepository.findByCitizen(citizen);
    }

    public List<Issue> getIssuesByAssignedAdmin(User assignedAdmin) {
        return issueRepository.findByAssignedAdmin(assignedAdmin);
    }

    public List<Issue> getIssuesByStatus(String status) {
        return issueRepository.findByStatus(status);
    }

    public Issue getIssueById(Long id) {
        // --- THIS IS THE BEST PRACTICE FIX ---
        // Let the service layer handle the "not found" case.
        // This will throw an exception that your GlobalExceptionHandler
        // will automatically catch and turn into a 404 page.
        return issueRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Issue not found with id: " + id));
    }

    public Issue saveIssue(Issue issue) {
        return issueRepository.save(issue);
    }

    public void deleteIssue(Long id) {
        issueRepository.deleteById(id);
    }

    public List<Issue> getIssuesByCitizenAndStatus(User citizen, String status) {
        return issueRepository.findByCitizenAndStatus(citizen, status);
    }

    public List<Issue> getIssuesByCategory(String category) {
        return issueRepository.findByCategory(category);
    }

    public Issue updateIssueStatus(Issue issue, String newStatus) {
        issue.setStatus(newStatus);
        if ("RESOLVED".equals(newStatus)) {
            issue.setResolvedAt(LocalDateTime.now());
        }
        return issueRepository.save(issue);
    }
}