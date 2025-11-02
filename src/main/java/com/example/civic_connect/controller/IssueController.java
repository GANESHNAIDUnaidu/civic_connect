package com.example.civic_connect.controller;

import com.example.civic_connect.model.Issue;
import com.example.civic_connect.model.IssueUpdate;
import com.example.civic_connect.model.User;
import com.example.civic_connect.service.IssueService;
import com.example.civic_connect.service.IssueUpdateService;
import com.example.civic_connect.service.RatingService; // Import this
import com.example.civic_connect.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/issues")
public class IssueController {

    private final IssueService issueService;
    private final IssueUpdateService issueUpdateService;
    private final UserService userService;
    private final RatingService ratingService; // Add this field

    // This is the (un-changing) list of categories
    private final List<String> allCategories = Arrays.asList(
        "Pothole", "Streetlight", "Garbage", "Water", 
        "Electricity", "Road", "Sewage", "Other"
    );

    // ADD RatingService TO THE CONSTRUCTOR
    public IssueController(IssueService issueService, 
                           IssueUpdateService issueUpdateService, 
                           UserService userService, 
                           RatingService ratingService) { // <-- Add here
        this.issueService = issueService;
        this.issueUpdateService = issueUpdateService;
        this.userService = userService;
        this.ratingService = ratingService; // <-- Add here
    }

    @GetMapping("/report")
    public String showReportForm(Model model) {
        model.addAttribute("issue", new Issue());
        model.addAttribute("allCategories", allCategories);
        return "report-issue";
    }

    @PostMapping("/report")
    public String reportIssue(@Valid @ModelAttribute("issue") Issue issue,
                              BindingResult bindingResult,
                              Model model,
                              Authentication auth) {

        if (bindingResult.hasErrors()) {
            // If validation fails, add categories back to the model
            model.addAttribute("allCategories", allCategories);
            return "report-issue";
        }

        String username = auth.getName();
        User citizen = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        issue.setCitizen(citizen);
        issue.setStatus("NEW");
        issueService.saveIssue(issue);

        return "redirect:/dashboard/my-dashboard";
    }

    @GetMapping("/{id}")
    public String viewIssue(@PathVariable Long id, Model model, Authentication auth) {
        
        Issue issue = issueService.getIssueById(id);
        if (issue == null) {
            throw new NoSuchElementException("Issue not found with id: " + id);
        }

        List<IssueUpdate> updates = issueUpdateService.getUpdatesByIssue(issue);

        String username = auth.getName();
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Determine if the rating form should be shown.
        boolean showRatingForm = false;
        if ("ROLE_CITIZEN".equals(currentUser.getRole()) &&
            "RESOLVED".equals(issue.getStatus()) &&
            !ratingService.existsByIssue(issue)) {
            
            showRatingForm = true;
        }

        model.addAttribute("issue", issue);
        model.addAttribute("updates", updates);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("showRatingForm", showRatingForm); // ADD BOOLEAN TO MODEL

        return "view-issue";
    }

    @PostMapping("/{issueId}/update")
    public String addUpdate(@PathVariable Long issueId,
                            @RequestParam String comment,
                            @RequestParam(required = false) String status,
                            RedirectAttributes redirectAttributes,
                            Authentication auth) {

        if (comment == null || comment.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Comment cannot be empty!");
            return "redirect:/issues/" + issueId;
        }

        if (comment.length() > 1000) {
            redirectAttributes.addFlashAttribute("error", "Comment is too long (max 1000 characters)!");
            return "redirect:/issues/" + issueId;
        }

        Issue issue = issueService.getIssueById(issueId);
        if (issue == null) {
            throw new NoSuchElementException("Issue not found with id: " + issueId);
        }

        String username = auth.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        IssueUpdate update = new IssueUpdate();
        update.setComment(comment);
        update.setIssue(issue);
        update.setUser(user);

        issueUpdateService.saveUpdate(update);

        if (status != null && !status.isEmpty() && !issue.getStatus().equals(status)) {
        issueService.updateIssueStatus(issue, status); // This method saves the issue
    }
        // --- THIS IS THE TYPO FIX ---
        return "redirect:/issues/" + issueId;
    }
}