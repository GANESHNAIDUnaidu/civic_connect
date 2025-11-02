package com.example.civic_connect.controller;

import com.example.civic_connect.model.Issue;
import com.example.civic_connect.model.ServiceRating;
import com.example.civic_connect.model.User;
import com.example.civic_connect.service.IssueService;
import com.example.civic_connect.service.RatingService;
import com.example.civic_connect.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import com.example.civic_connect.service.SentimentAnalysisService;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final IssueService issueService;
    private final UserService userService;
    private final RatingService ratingService;
    private final SentimentAnalysisService sentimentService;

    public DashboardController(IssueService issueService, UserService userService, RatingService ratingService,SentimentAnalysisService sentimentService) {
        this.issueService = issueService;
        this.userService = userService;
        this.ratingService = ratingService;
        this.sentimentService = sentimentService;
    }

    @GetMapping("/my-dashboard")
    public String myDashboard(Model model, Authentication auth) {
        String username = auth.getName();
        User user = userService.findByUsername(username)
                // --- SYNTAX FIX ---
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        if ("ROLE_ADMIN".equals(user.getRole())) {
            // --- ADMIN LOGIC ---
            List<Issue> allIssues = issueService.getAllIssues();
            
            long totalIssueCount = allIssues.size();
            long newIssueCount = allIssues.stream().filter(issue -> "NEW".equals(issue.getStatus())).count();
            long inProgressIssueCount = allIssues.stream().filter(issue -> "IN_PROGRESS".equals(issue.getStatus())).count();
            long resolvedIssueCount = allIssues.stream().filter(issue -> "RESOLVED".equals(issue.getStatus())).count();

            List<String> categories = Arrays.asList(
                "Pothole", "Streetlight", "Garbage", "Water", 
                "Electricity", "Road", "Sewage", "Other"
            );
            
            model.addAttribute("issues", allIssues);
            model.addAttribute("user", user);
            
            model.addAttribute("totalIssueCount", totalIssueCount);
            model.addAttribute("newIssueCount", newIssueCount);
            model.addAttribute("inProgressIssueCount", inProgressIssueCount);
            model.addAttribute("resolvedIssueCount", resolvedIssueCount);
            model.addAttribute("allCategories", categories);
            
            return "admin-dashboard";

        } else {
            // --- CITIZEN LOGIC ---
            List<Issue> myIssues = issueService.getIssuesByCitizen(user);

            long totalIssueCount = myIssues.size();
            long newIssueCount = myIssues.stream().filter(issue -> "NEW".equals(issue.getStatus())).count();
            long inProgressIssueCount = myIssues.stream().filter(issue -> "IN_PROGRESS".equals(issue.getStatus())).count();
            long resolvedIssueCount = myIssues.stream().filter(issue -> "RESOLVED".equals(issue.getStatus())).count();

            model.addAttribute("issues", myIssues);
            model.addAttribute("user", user);

            model.addAttribute("totalIssueCount", totalIssueCount);
            model.addAttribute("newIssueCount", newIssueCount);
            model.addAttribute("inProgressIssueCount", inProgressIssueCount);
            model.addAttribute("resolvedIssueCount", resolvedIssueCount);

            return "citizen-dashboard";
        }
    }

    @PostMapping("/rate-issue/{issueId}")
    public String rateIssue(@PathVariable Long issueId,
                             @RequestParam int rating,
                             @RequestParam(required = false) String comment,
                             RedirectAttributes redirectAttributes,
                             Authentication auth) {

        String username = auth.getName();
        User citizen = userService.findByUsername(username)
                 .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Issue issue = issueService.getIssueById(issueId);
        if (issue == null) {
            throw new NoSuchElementException("Issue not found with id: " + issueId);
        }

        // --- (All your validation logic is the same) ---
        if (!"RESOLVED".equals(issue.getStatus())) {
            redirectAttributes.addFlashAttribute("error", "Issue must be resolved before rating!");
            return "redirect:/dashboard/my-dashboard";
        }
        if (ratingService.existsByIssue(issue)) {
            redirectAttributes.addFlashAttribute("error", "Issue already rated!");
            return "redirect:/dashboard/my-dashboard";
        }
        if (rating < 1 || rating > 5) {
            redirectAttributes.addFlashAttribute("error", "Rating must be between 1 and 5!");
            return "redirect:/dashboard/my-dashboard";
        }

        // Save the rating
        ServiceRating serviceRating = new ServiceRating();
        serviceRating.setRating(rating);
        serviceRating.setComment(comment);
        serviceRating.setIssue(issue);
        serviceRating.setCitizen(citizen);
        ratingService.saveRating(serviceRating);
        
        // --- THIS IS THE NEW LOGIC ---
        // 5. Analyze and save the sentiment
        if (comment != null && !comment.trim().isEmpty()) {
            try {
                // Call the API
                String sentiment = sentimentService.analyzeSentiment(comment);
                // Save the result to the Issue entity
                issue.setSentiment(sentiment);
                issueService.saveIssue(issue); // Re-save the issue with the sentiment
            } catch (Exception e) {
                // Don't crash the user's request if the API fails
                System.err.println("Could not analyze sentiment: " + e.getMessage());
            }
        }

        redirectAttributes.addFlashAttribute("success", "Issue rated successfully!");
        return "redirect:/dashboard/my-dashboard";
    }
}