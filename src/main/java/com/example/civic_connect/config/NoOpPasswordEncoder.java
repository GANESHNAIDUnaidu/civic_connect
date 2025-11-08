package com.example.civic_connect.config; // Or any config package

import org.springframework.security.crypto.password.PasswordEncoder;

public class NoOpPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        // Just return the password as a plain string
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        // Just check if the two strings are equal
        return rawPassword.toString().equals(encodedPassword);
    }
    
    // You can also just return 'this' as a singleton
    public static PasswordEncoder getInstance() {
        return new NoOpPasswordEncoder();
    }
}