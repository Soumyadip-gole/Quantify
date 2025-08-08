package com.quantify.quantify_backend.repository;

import com.quantify.quantify_backend.model.transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface transaction_repo extends JpaRepository<transaction, String> {
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

    transaction findByTransactionId(String transactionId);

    // Fixed: transaction entity relates to balance, which relates to user
    // To find by user ID, we need to go through the balance relationship
    List<transaction> findByBalance_User_UserId(String userId);

    transaction findBySymbol(String symbol);

    // This method doesn't make sense as transaction doesn't have holdingId field
    // Remove or replace with appropriate field reference
    // transaction findByHoldingId(String holdingId);
}
