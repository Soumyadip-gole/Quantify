package com.quantify.quantify_backend.controller;

import com.quantify.quantify_backend.model.user;
import com.quantify.quantify_backend.repository.user_repo;
import com.rabbitmq.client.Return;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class login {

    @Autowired
    user_repo userRepo;

    @GetMapping("/goole-login")
    public String googleLogin() {
        return "<a href=\"/oauth2/authorization/google\">Login with Google</a>";
    }

    @GetMapping("/google-login-callback")
    public String googleLoginCallback(@AuthenticationPrincipal OAuth2User user) {
            if (user != null) {
                String googleId = user.getAttribute("sub");
                user existUser = userRepo.findByGoogleId(googleId);
                System.out.println(user);
                if (existUser == null) {
                    user newUser = new user();
                    String email = user.getAttribute("email");
                    String name = user.getAttribute("name");
                    newUser.setGoogleId(googleId);
                    newUser.setEmail(email);
                    newUser.setUsername(name);
                    newUser.setPassword("");
                    userRepo.save(newUser);
                }

                return "Welcome back, " + user.getAttribute("name") + "! You are logged in. <a href=\"/logout\">Logout</a><a href=\"/\">Home</a>";
            }
        return "error";
    }
}
