package com.example.civic_connect.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.Objects; // Import this

@Entity
@Table(name = "service_ratings")
public class ServiceRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    @Column(nullable = false)
    private int rating; // 1-5

    // --- BEST PRACTICE FIX ---
    // Added @Column(length = 500) to match the @Size
    @Size(max = 500, message = "Comment cannot exceed 500 characters")
    @Column(length = 500)
    private String comment;

    // One rating is for one issue
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", nullable = false, unique = true)
    @NotNull(message = "Issue is required")
    private Issue issue;

    // One rating is from one citizen
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "citizen_id", nullable = false)
    @NotNull(message = "Citizen is required")
    private User citizen;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Default constructor
    public ServiceRating() {}

    // Constructor with required fields
    public ServiceRating(int rating, String comment, Issue issue, User citizen) {
        this.rating = rating;
        this.comment = comment;
        this.issue = issue;
        this.citizen = citizen;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
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

    public User getCitizen() {
        return citizen;
    }

    public void setCitizen(User citizen) {
        this.citizen = citizen;
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
        ServiceRating that = (ServiceRating) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : 31;
    }

    @Override
    public String toString() {
        return "ServiceRating{" +
                "id=" + id +
                ", rating=" + rating +
                ", issueId=" + (issue != null ? issue.getId() : "null") +
                ", citizenId=" + (citizen != null ? citizen.getId() : "null") +
                '}';
    }
}