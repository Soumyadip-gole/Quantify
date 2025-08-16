package com.quantify.quantify_backend.controller;

import com.quantify.quantify_backend.model.user;
import com.quantify.quantify_backend.model.balance; // Import the balance model
import com.quantify.quantify_backend.repository.user_repo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class auth {

    @Autowired
    private user_repo userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String email = request.get("email");
        String password = request.get("password");

        if (userRepository.findByEmail(email)!=null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User with this email already exists"));
        }

        user newUser = new user();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setGoogleId(null);

        balance defaultBalance = new balance(0); // Start with 0 balance
        defaultBalance.setUser(newUser);
        newUser.setBalance(defaultBalance);

        userRepository.save(newUser);

        return ResponseEntity.ok(Map.of("message", "Registration successful"));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String password = requestBody.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and password are required"));
        }

        try {

            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            HttpSession session = request.getSession(true); // Create session if it doesn't exist
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
            session.setAttribute("AUTHENTICATED_USER", email);

            // Check if JSESSIONID cookie is being set
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                System.out.println("cookies present in request");
            } else {
                System.out.println("No existing cookies found");
            }

            // Get user details
            user currentUser = userRepository.findByEmail(email);
            if (currentUser == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            }

            // Safety check: Create balance if it doesn't exist for existing users
            if (currentUser.getBalance() == null) {
                balance defaultBalance = new balance(0);
                defaultBalance.setUser(currentUser);
                currentUser.setBalance(defaultBalance);
                userRepository.save(currentUser);
                System.out.println("Created missing balance for existing user: " + email);
            }

            return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "username", currentUser.getUsername(),
                "email", email,
                "provider", "local",
                "userId", currentUser.getUserId(),
                "sessionId", session.getId() // Add session ID to response for debugging
            ));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getCurrentUser(HttpServletRequest request,
                                                            @AuthenticationPrincipal OAuth2User oAuth2User,
                                                            @AuthenticationPrincipal UserDetails userDetails) {

        // Check session
        HttpSession session = request.getSession(false);
        if (session != null) {
            System.out.println("session found with ID: " + session.getId());
        } else {
            System.out.println("No session found!");
        }

        // Check cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            System.out.println(" cookies present in request:");
        } else {
            System.out.println("No cookies found in request!");
        }

        // Check authentication principals
        System.out.println("OAuth2User: " + (oAuth2User != null ? "present" : "null"));
        System.out.println("UserDetails: " + (userDetails != null ? "present" : "null"));

        // Check SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("SecurityContext Authentication: " + (auth != null ? auth.getName() : "null"));

        if (oAuth2User != null) {
            String email = oAuth2User.getAttribute("email");
            System.out.println("Looking for Google user with email: " + email);

            user currentUser = userRepository.findByEmail(email);
            System.out.println("User found in database: " + (currentUser != null));

            if (currentUser == null) {
                System.err.println("ERROR: Google user not found in database despite successful OAuth2 login!");
                return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            }

            System.out.println("Returning Google user info for: " + email);
            return ResponseEntity.ok(Map.of(
                "username", currentUser.getUsername(),
                "email", email,
                "provider", "google",
                "userId", currentUser.getUserId()
            ));

        } else if (userDetails != null) {
            String email = userDetails.getUsername();
            user currentUser = userRepository.findByEmail(email);

            if (currentUser == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            }

            return ResponseEntity.ok(Map.of(
                "username", currentUser.getUsername(),
                "email", email,
                "provider", "local",
                    "userId", currentUser.getUserId()
            ));
        }

        return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
    }

    // Google OAuth login endpoints - keeping for compatibility
    @GetMapping("/google-login")
    public String googleLogin() {
        return "<a href=\"/oauth2/authorization/google\">Login with Google</a>";
    }
}