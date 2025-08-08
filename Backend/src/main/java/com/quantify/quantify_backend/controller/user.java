package com.quantify.quantify_backend.controller;

import com.quantify.quantify_backend.repository.balance_repo;
import com.quantify.quantify_backend.repository.holding_repo;
import com.quantify.quantify_backend.repository.transaction_repo;
import com.quantify.quantify_backend.repository.user_repo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class user {
    //to handle protfolio and balance and transactions

    @Autowired
    private holding_repo holdingRepo;

    @Autowired
    private user_repo userRepository;

    @Autowired
    private balance_repo balanceRepo;

    @Autowired
    private transaction_repo transactionRepo;

    @GetMapping("/portfolio")
    public ResponseEntity<Map<String,Object>> getPortfolio(@RequestBody Map<String,String> request) {
        String userId = request.get("userId");
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "User ID is required"));
        }

        // Find user by userId (much more efficient than email lookup)
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        }

        var user = userOpt.get();

        // Fetch holdings, balance, and transactions for the user
        var holdings = holdingRepo.findByUser_UserId(user.getUserId());
        // Get balance from user entity since user owns the relationship
        var balance = user.getBalance();
        var transactions = transactionRepo.findByBalance_User_UserId(user.getUserId());

        // Construct response with null-safe handling
        Map<String, Object> response = Map.of(
            "holdings", holdings != null ? holdings : "No holdings found",
            "balance", balance != null ? balance : "No balance found",
            "transactions", transactions != null ? transactions : "No transactions found"
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/updateBalance")
    public ResponseEntity<Map<String, String>> updateBalance(@RequestBody Map<String, Object> request) {
        String userId = (String) request.get("userId");
        Object addingObj = request.get("add");

        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "User ID is required"));
        }
        if (addingObj == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Adding amount is required"));
        }

        // Convert addingObj to Double (handle both Integer and Double)
        Double adding;
        try {
            if (addingObj instanceof Integer) {
                adding = ((Integer) addingObj).doubleValue();
            } else if (addingObj instanceof Double) {
                adding = (Double) addingObj;
            } else if (addingObj instanceof Number) {
                adding = ((Number) addingObj).doubleValue();
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid adding amount format"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid adding amount format"));
        }

        // Find user by userId (more efficient)
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        }

        var user = userOpt.get();

        // Update balance - get balance from user entity
        var balance = user.getBalance();
        if (balance == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Balance not found for user"));
        }
        balance.setBalance(balance.getBalance() + adding);
        balanceRepo.save(balance);
        return ResponseEntity.ok(Map.of("message", "Balance updated successfully"));
    }
}
