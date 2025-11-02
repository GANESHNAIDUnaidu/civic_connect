package com.example.civic_connect.service;

import com.example.civic_connect.model.IssueUpdate;
import com.example.civic_connect.repository.IssueUpdateRepository;
// import org.springframework.beans.factory.annotation.Autowired; // No longer needed
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IssueUpdateService {

    private final IssueUpdateRepository issueUpdateRepository;

    // Use constructor injection
    public IssueUpdateService(IssueUpdateRepository issueUpdateRepository) {
        this.issueUpdateRepository = issueUpdateRepository;
    }

    public List<IssueUpdate> getAllUpdates() {
        return issueUpdateRepository.findAll();
    }

    public List<IssueUpdate> getUpdatesByIssue(com.example.civic_connect.model.Issue issue) {
        return issueUpdateRepository.findByIssueOrderByCreatedAtAsc(issue);
    }

    public IssueUpdate saveUpdate(IssueUpdate update) {
        return issueUpdateRepository.save(update);
    }

    public void deleteUpdate(Long id) {
        issueUpdateRepository.deleteById(id);
    }
}