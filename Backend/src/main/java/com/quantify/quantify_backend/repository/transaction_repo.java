package com.quantify.quantify_backend.repository;

import com.quantify.quantify_backend.model.transaction;
import org.springframework.data.jpa.repository.JpaRepository;

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

    transaction findByUserId(String userId);

    transaction findBySymbol(String symbol);

    transaction findByHoldingId(String holdingId);

    transaction findByType(String type);
}
