package com.example.civic_connect.repository;

import com.example.civic_connect.model.ServiceRating;
import com.example.civic_connect.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository; // No longer needed
import java.util.Optional;

// @Repository annotation is not needed
public interface RatingRepository extends JpaRepository<ServiceRating, Long> {
    Optional<ServiceRating> findByIssue(Issue issue);
    boolean existsByIssue(Issue issue);
}