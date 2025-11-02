package com.example.civic_connect.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects; // Import this

@Entity
@Table(name = "issues")
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    @Column(nullable = false)
    private String title;

    // --- BEST PRACTICE FIX ---
    // Use @Column(length=...) for strings under 4000-8000 chars.
    // @Lob is inefficient for only 2000 chars.
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    @Column(length = 2000) 
    private String description;

    @NotBlank(message = "Category is required")
    @Size(max = 50, message = "Category cannot exceed 50 characters")
    @Column(nullable = false)
    private String category; // "Pothole", "Streetlight", etc.

    // @NotBlank(message = "Status is required")
    @Size(max = 20, message = "Status cannot exceed 20 characters")
    @Column(nullable = false)
    private String status; // "NEW", "IN_PROGRESS", "RESOLVED"

    private Double latitude;
    private Double longitude;
    private String imageUrl;

    @Size(max = 20, message = "Sentiment cannot exceed 20 characters")
    @Column(nullable = true)
    private String sentiment; // (Bonus) "POSITIVE", "NEGATIVE"

    // Many issues can be reported by one citizen
    @ManyToOne(fetch = FetchType.LAZY) // Good: Default is EAGER
    @JoinColumn(name = "citizen_id", nullable = false)
    // @NotNull(message = "Citizen is required")
    private User citizen;

    // Many issues can be assigned to one admin
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private User assignedAdmin;

    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    // One issue can have many updates
    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<IssueUpdate> updates;

    // One issue can have one rating
    @OneToOne(mappedBy = "issue", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ServiceRating rating;

    // Default constructor
    public Issue() {}

    // Constructor with required fields
    public Issue(String title, String description, String category, User citizen) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.citizen = citizen;
        this.status = "NEW";
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public User getCitizen() {
        return citizen;
    }

    public void setCitizen(User citizen) {
        this.citizen = citizen;
    }

    public User getAssignedAdmin() {
        return assignedAdmin;
    }

    public void setAssignedAdmin(User assignedAdmin) {
        this.assignedAdmin = assignedAdmin;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public List<IssueUpdate> getUpdates() {
        return updates;
    }

    public void setUpdates(List<IssueUpdate> updates) {
        this.updates = updates;
    }

    public ServiceRating getRating() {
        return rating;
    }

    public void setRating(ServiceRating rating) {
        this.rating = rating;
    }

    // --- BEST PRACTICE: equals(), hashCode(), and toString() ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Issue issue = (Issue) o;
        // Only use the ID for equals, and check for null if the entity is new
        return id != null && id.equals(issue.id);
    }

    @Override
    public int hashCode() {
        // Use a constant hash code for new (transient) entities
        // and the ID's hash code for persisted entities.
        return id != null ? Objects.hash(id) : 31;
    }

    @Override
    public String toString() {
        return "Issue{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                ", citizen=" + (citizen != null ? citizen.getId() : "null") +
                ", createdAt=" + createdAt +
                '}';
    }
}