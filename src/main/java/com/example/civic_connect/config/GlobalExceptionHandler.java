package com.example.civic_connect.config;

import org.springframework.http.HttpStatus; // Import this
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus; // Import this
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // Add this
    public String handleNoSuchElementException(NoSuchElementException ex, Model model) {
        model.addAttribute("error", "Resource not found: " + ex.getMessage());
        return "error"; // Renders error.html
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // Add this
    public String handleNotFoundException(NoHandlerFoundException ex, Model model) {
        model.addAttribute("error", "Page not found: " + ex.getRequestURL());
        return "error"; // Renders error.html
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // Add this
    public String handleGenericException(Exception ex, Model model) {
        // It's good practice to log the exception here
        // log.error("Unexpected error", ex); 
        model.addAttribute("error", "An unexpected error occurred: " + ex.getMessage());
        return "error"; // Renders error.html
    }
}