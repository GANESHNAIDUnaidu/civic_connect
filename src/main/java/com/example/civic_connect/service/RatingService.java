package com.example.civic_connect.service;

import com.example.civic_connect.model.ServiceRating;
import com.example.civic_connect.repository.RatingRepository;
// import org.springframework.beans.factory.annotation.Autowired; // No longer needed
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;

    // Use constructor injection
    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public ServiceRating saveRating(ServiceRating rating) {
        return ratingRepository.save(rating);
    }

    public Optional<ServiceRating> getRatingByIssue(com.example.civic_connect.model.Issue issue) {
        return ratingRepository.findByIssue(issue);
    }

    public boolean existsByIssue(com.example.civic_connect.model.Issue issue) {
        return ratingRepository.existsByIssue(issue);
    }

    public Optional<ServiceRating> findById(Long id) {
        return ratingRepository.findById(id);
    }
}