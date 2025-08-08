package com.quantify.quantify_backend.repository;

import com.quantify.quantify_backend.model.watchlist_items;
import org.springframework.data.jpa.repository.JpaRepository;

public interface watchlist_items_repo extends JpaRepository<watchlist_items, String> {
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

    // Fixed: parameter name should match what it actually represents
    watchlist_items findByWatchlist_WatchlistId(String watchlistId);
    watchlist_items findBySymbol(String symbol);
}
