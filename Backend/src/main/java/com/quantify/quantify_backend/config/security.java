package com.quantify.quantify_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class security {

    @Bean
    public SecurityFilterChain sec(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> {
                    oauth.defaultSuccessUrl("/", true);
                })
                .logout(l -> l
                        .logoutSuccessUrl("/")
                );

        return http.build();
    }
}
