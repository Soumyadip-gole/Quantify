package com.quantify.quantify_backend.repository;

import com.quantify.quantify_backend.model.watchlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface watchlist_repo extends JpaRepository<watchlist, String> {
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

    // Fixed: watchlist entity has a 'user' relationship, so we need to reference user.userId
    List<watchlist> findByUser_UserId(String userId);

    // This method doesn't make sense as watchlist doesn't have a direct symbol field
    // Watchlist has items, and items might have symbols
    // Remove this method or replace with appropriate query
    // watchlist findBySymbol(String symbol);
}
