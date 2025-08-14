package com.quantify.quantify_backend.controller;

import com.quantify.quantify_backend.model.user;
import com.quantify.quantify_backend.model.watchlist;
import com.quantify.quantify_backend.model.watchlist_items;
import com.quantify.quantify_backend.repository.user_repo;
import com.quantify.quantify_backend.repository.watchlist_repo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/watchlist")
public class watchlist_controller {

    @Autowired
    private watchlist_repo watchlistRepository;

    @Autowired
    private user_repo userRepository;

    @PostMapping("/create")
    public String createWatchlist(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        String watchlistName = request.get("name");
        if (userId == null || watchlistName == null) {
            return "User ID and watchlist name are required";
        }

        Optional<user> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return "User not found";
        }
        user currentUser = optionalUser.get();

        watchlist newWatchlist = new watchlist();
        newWatchlist.setName(watchlistName);
        newWatchlist.setUser(currentUser); // Set the user object

        List<watchlist> userWatchlists = currentUser.getWatchlists();
        if (userWatchlists == null) {
            userWatchlists = new ArrayList<>();
        }
        userWatchlists.add(newWatchlist);
        currentUser.setWatchlists(userWatchlists);

        userRepository.save(currentUser); // Save the user to persist the new watchlist relationship

        return "Watchlist created successfully";
    }

    @PostMapping("/add-symbol")
    public String addSymbolToWatchlist(@RequestBody Map<String, String> request) {
        String watchlistId = request.get("watchlistId");
        String symbol = request.get("symbol");
        if (watchlistId == null || symbol == null) {
            return "Watchlist ID and symbol are required";
        }
        Optional<watchlist> optionalWatchlist = watchlistRepository.findById(watchlistId);
        if (optionalWatchlist.isEmpty()) {
            return "Watchlist not found";
        }
        watchlist currentWatchlist = optionalWatchlist.get();
        List<watchlist_items> items = currentWatchlist.getItems();
        if (items == null) {
            items = new ArrayList<>();
        }
        watchlist_items newItem = new watchlist_items(symbol);
        newItem.setWatchlist(currentWatchlist); // Set the watchlist for the item
        items.add(newItem);
        currentWatchlist.setItems(items);
        watchlistRepository.save(currentWatchlist); // Save the watchlist to persist the new item
        return "Symbol added to watchlist successfully";
    }

    @GetMapping("/all")
    public List<watchlist> getAllWatchlists(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        if (userId == null) {
            return new ArrayList<>(); // Return empty list if userId is not provided
        }
        Optional<user> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return new ArrayList<>(); // Return empty list if user not found
        }
        user currentUser = optionalUser.get();
        return currentUser.getWatchlists(); // Return the user's watchlists
    }

    @GetMapping("/{watchlistId}")
    public watchlist getWatchlistById(@PathVariable String watchlistId) {
        Optional<watchlist> optionalWatchlist = watchlistRepository.findById(watchlistId);
        if (optionalWatchlist.isEmpty()) {
            return null; // Return null if watchlist not found
        }
        return optionalWatchlist.get(); // Return the watchlist
    }

    @DeleteMapping("/delete/{watchlistId}")
    public String deleteWatchlist(@PathVariable String watchlistId) {
        Optional<watchlist> optionalWatchlist = watchlistRepository.findById(watchlistId);
        if (optionalWatchlist.isEmpty()) {
            return "Watchlist not found";
        }
        watchlist currentWatchlist = optionalWatchlist.get();
        watchlistRepository.delete(currentWatchlist); // Delete the watchlist
        return "Watchlist deleted successfully";
    }

    @DeleteMapping("/remove-symbol")
    public String removeSymbolFromWatchlist(@RequestBody Map<String, String> request) {
        String watchlistId = request.get("watchlistId");
        String symbol = request.get("symbol");
        if (watchlistId == null || symbol == null) {
            return "Watchlist ID and symbol are required";
        }
        Optional<watchlist> optionalWatchlist = watchlistRepository.findById(watchlistId);
        if (optionalWatchlist.isEmpty()) {
            return "Watchlist not found";
        }
        watchlist currentWatchlist = optionalWatchlist.get();
        List<watchlist_items> items = currentWatchlist.getItems();
        if (items == null || items.isEmpty()) {
            return "No items found in watchlist";
        }
        boolean removed = items.removeIf(item -> item.getSymbol() != null && item.getSymbol().equalsIgnoreCase(symbol));
        if (!removed) {
            return "Symbol not found in watchlist";
        }
        currentWatchlist.setItems(items);
        watchlistRepository.save(currentWatchlist);
        return "Symbol removed from watchlist successfully";
    }

    // Minimalistic update endpoint
    @PatchMapping("/update-name")
    public String updateWatchlistName(@RequestBody Map<String, String> request) {
        String watchlistId = request.get("watchlistId");
        String newName = request.get("newName");
        if (watchlistId == null || newName == null) {
            return "Watchlist ID and new name are required";
        }
        Optional<watchlist> optionalWatchlist = watchlistRepository.findById(watchlistId);
        if (optionalWatchlist.isEmpty()) {
            return "Watchlist not found";
        }
        watchlist wl = optionalWatchlist.get();
        wl.setName(newName);
        watchlistRepository.save(wl);
        return "Watchlist name updated successfully";
    }
}
