package com.quantify.quantify_backend.config;

import com.quantify.quantify_backend.model.balance;
import com.quantify.quantify_backend.model.user;
import com.quantify.quantify_backend.repository.user_repo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private user_repo userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        System.out.println("=== OAuth2LoginSuccessHandler CALLED ===");

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String googleId = oAuth2User.getAttribute("sub"); // Google's unique user ID

        System.out.println("Google OAuth2 User Info:");
        System.out.println("  Email: " + email);
        System.out.println("  Name: " + name);
        System.out.println("  Google ID: " + googleId);

        // Check if user already exists
        user existingUser = userRepository.findByEmail(email);
        System.out.println("Existing user found: " + (existingUser != null));

        if (existingUser == null) {
            System.out.println("Creating new Google user...");
            // Create new user for first-time Google login
            user newUser = new user();
            newUser.setEmail(email);
            newUser.setUsername(name != null ? name : email.split("@")[0]); // Use name or fallback to email prefix
            newUser.setGoogleId(googleId);
            newUser.setPassword(null); // No password for OAuth users

            // Create default balance for new Google user
            balance defaultBalance = new balance(0); // Start with 0 balance
            defaultBalance.setUser(newUser);
            newUser.setBalance(defaultBalance);

            try {
                user savedUser = userRepository.save(newUser);
                System.out.println("Successfully created new Google user: " + email + " with ID: " + savedUser.getUserId());
            } catch (Exception e) {
                System.err.println("Error creating Google user: " + e.getMessage());
                e.printStackTrace();
            }
        } else if (existingUser.getGoogleId() == null) {
            System.out.println("Updating existing local user with Google ID...");
            // Update existing local user with Google ID
            existingUser.setGoogleId(googleId);

            // Create balance if it doesn't exist
            if (existingUser.getBalance() == null) {
                balance defaultBalance = new balance(0);
                defaultBalance.setUser(existingUser);
                existingUser.setBalance(defaultBalance);
            }

            try {
                userRepository.save(existingUser);
                System.out.println("Updated existing user: " + email + " with Google ID and balance");
            } catch (Exception e) {
                System.err.println("Error updating existing user: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Google user already exists: " + email);
        }

        System.out.println("=== OAuth2LoginSuccessHandler COMPLETE ===");

        // Continue with the default behavior
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
