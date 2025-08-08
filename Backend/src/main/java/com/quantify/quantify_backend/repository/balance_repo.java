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

    // Since balance is mapped by "balance" in user entity, 
    // and user has @JoinColumn(name="balance_id") pointing to balance,
    // we can't directly query balance by user_id. Instead we use a custom query or find by balance_id
    // For now, remove this method and use findById with the balance_id from user entity
    // balance findByUser_UserId(String userId);
}
