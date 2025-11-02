package com.example.civic_connect.controller;

import com.example.civic_connect.model.User;
import com.example.civic_connect.repository.UserRepository;
import jakarta.validation.Valid;
// import org.springframework.beans.factory.annotation.Autowired; // No longer needed
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    // Use final fields for dependencies
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Use constructor injection
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String home(Model model) {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, 
                               BindingResult bindingResult) { // Model is no longer needed here

        // --- IMPROVEMENT ---
        // Check for uniqueness and add field-specific errors
        if (userRepository.existsByUsername(user.getUsername())) {
            bindingResult.rejectValue("username", "user.username.exists", "Username already exists!");
        }
        
        if (userRepository.existsByEmail(user.getEmail())) {
            bindingResult.rejectValue("email", "user.email.exists", "Email already exists!");
        }

        // Check for validation errors (from @Valid) AND our uniqueness errors
        if (bindingResult.hasErrors()) {
            return "register"; // Return to form, now with field-specific errors
        }
        
        // Encode password and set role
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_CITIZEN"); // Default role for new users

        userRepository.save(user);
        
        // Add a success message parameter for the login page
        return "redirect:/login?registered=true";
    }
}