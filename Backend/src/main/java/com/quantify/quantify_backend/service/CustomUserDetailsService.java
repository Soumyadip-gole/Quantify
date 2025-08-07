package com.quantify.quantify_backend.service;

import com.quantify.quantify_backend.model.user;
import com.quantify.quantify_backend.repository.user_repo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private user_repo userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        user userEntity = userRepository.findByEmail(email);

        if (userEntity == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        // Return Spring Security's User object
        return User.builder()
                .username(userEntity.getEmail()) // Use email as username
                .password(userEntity.getPassword()) // Encoded password from database
                .authorities(new ArrayList<>()) // Empty authorities for now, can add roles later
                .build();
    }
}
