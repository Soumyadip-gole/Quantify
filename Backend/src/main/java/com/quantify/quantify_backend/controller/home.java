package com.quantify.quantify_backend.controller;

import com.quantify.quantify_backend.model.balance;
import com.quantify.quantify_backend.model.user;
import com.quantify.quantify_backend.repository.user_repo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class home {

    @Autowired
    private user_repo userRepo;

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OAuth2User user) {
        if (user != null)
            return "Welcome back, " + user.getAttribute("name") + "! You are logged in. <a href=\"/auth/logout\">Logout</a><a href=\"/auth/user\">user</a>";

        return "Welcome to Quantify! <a href=\"/google-login\">Login with Google</a><a href=\"/auth/user\">user</a>";
    }

}
