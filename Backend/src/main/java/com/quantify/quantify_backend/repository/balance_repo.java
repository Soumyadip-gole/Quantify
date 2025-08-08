package com.quantify.quantify_backend.repository;

import com.quantify.quantify_backend.model.balance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface balance_repo extends JpaRepository<balance, String> {
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

    balance findByUserId(String userId);
}
