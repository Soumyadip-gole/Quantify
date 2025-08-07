package com.quantify.quantify_backend.repository;

import com.quantify.quantify_backend.model.user;
import org.springframework.data.jpa.repository.JpaRepository;

public interface user_repo extends JpaRepository<user,String> {
    // JpaRepository provides methods like:
    // save(S entity)
    // findById(ID id)
    // findAll()
    // findAllById(Iterable<ID> ids)
    // deleteById(ID id)
    // delete(T entity)
    // deleteAll()
    // count()
    // existsById(ID id)

    // Additional custom methods can be defined here if needed

    user findByEmail(String email);
    user findByGoogleId(String googleId);
    user findByUsername(String username);
    user findByUserId(String userId);
}
