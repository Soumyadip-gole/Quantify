package com.quantify.quantify_backend.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class home {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> index(@AuthenticationPrincipal OAuth2User user) {
        if (user != null) {
            Object n = user.getAttribute("name");
            String name = n != null ? n.toString() : "User";
            String html = """
                    <!doctype html>
                    <html lang="en">
                    <head>
                      <meta charset="utf-8">
                      <title>Quantify</title>
                      <style>
                        body{font-family:system-ui,-apple-system,Segoe UI,Roboto,Ubuntu,'Helvetica Neue',Arial,sans-serif;margin:2rem;line-height:1.5}
                        a{margin-right:1rem}
                        nav{margin-top:1rem}
                      </style>
                    </head>
                    <body>
                      <h1>Welcome back, %s!</h1>
                      <p>You are logged in.</p>
                      <nav>
                        <a href="/auth/logout">Logout</a>
                        <a href="/auth/user">User</a>
                        <a href="/swagger-ui/index.html">View API docs</a>
                      </nav>
                    </body>
                    </html>
                    """.formatted(name);
            return ResponseEntity.ok(html);
        }
        String html = """
                <!doctype html>
                <html lang="en">
                <head>
                  <meta charset="utf-8">
                  <title>Quantify</title>
                  <style>
                    body{font-family:system-ui,-apple-system,Segoe UI,Roboto,Ubuntu,'Helvetica Neue',Arial,sans-serif;margin:2rem;line-height:1.5}
                    a{margin-right:1rem}
                    nav{margin-top:1rem}
                  </style>
                </head>
                <body>
                  <h1>Welcome to Quantify!</h1>
                  <p>Please sign in to continue.</p>
                  <nav>
                    <a href="/auth/google-login">Login with Google</a>
                    <a href="/auth/user">User</a>
                    <a href="/swagger-ui/index.html">View API docs</a>
                  </nav>
                </body>
                </html>
                """;
        return ResponseEntity.ok(html);
    }

}
