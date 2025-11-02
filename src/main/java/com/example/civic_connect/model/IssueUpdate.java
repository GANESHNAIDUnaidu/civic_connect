package com.example.civic_connect.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.Objects; // Import this

@Entity
@Table(name = "issue_updates")
public class IssueUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- BEST PRACTICE FIX ---
    // Replaced @Lob with @Column(length = 1000) for efficiency
    @NotBlank(message = "Comment is required")
    @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
    @Column(nullable = false, length = 1000)
    private String comment;

    // Many updates belong to one issue
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", nullable = false)
    @NotNull(message = "Issue is required")
    private Issue issue;

    // Many updates can be made by one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    private User user;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Default constructor
    public IssueUpdate() {}

    // Constructor with required fields
    public IssueUpdate(String comment, Issue issue, User user) {
        this.comment = comment;
        this.issue = issue;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // --- BEST PRACTICE: equals(), hashCode(), and toString() ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IssueUpdate that = (IssueUpdate) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : 31;
    }

    @Override
    public String toString() {
        return "IssueUpdate{" +
                "id=" + id +
                ", comment='" + (comment.length() > 50 ? comment.substring(0, 50) + "..." : comment) + '\'' +
                ", issueId=" + (issue != null ? issue.getId() : "null") +
                ", userId=" + (user != null ? user.getId() : "null") +
                ", createdAt=" + createdAt +
                '}';
    }
}