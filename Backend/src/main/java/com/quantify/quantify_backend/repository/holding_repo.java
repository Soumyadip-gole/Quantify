package com.quantify.quantify_backend.repository;

import com.quantify.quantify_backend.model.holding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface holding_repo extends JpaRepository<holding, String> {
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

    // Fixed: Changed from findByUser_Id to findByUser_UserId to match the actual field name
    List<holding> findByUser_UserId(String userId);
    holding findByHoldingId(String holdingId);
    holding findBySymbol(String symbol);
}
