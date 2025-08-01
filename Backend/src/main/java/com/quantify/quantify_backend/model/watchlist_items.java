package com.quantify.quantify_backend.model;


import jakarta.persistence.*;

@Entity
@Table(name="watchlist_items")
public class watchlist_items {

    @Id
    @Column(name="item_id")
    private String itemId;

    @Column(name="symbol")
    private String symbol;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name="watchlist_id")
    private watchlist watchlist;

    public watchlist getWatchlist() {
        return watchlist;
    }

    public void setWatchlist(watchlist watchlist) {
        this.watchlist = watchlist;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    @Override
    public String toString() {
        return "watchlist_items{" +
                "itemId='" + itemId + '\'' +
                ", symbol='" + symbol + '\'' +
                '}';
    }

    public watchlist_items(String symbol) {
        this.symbol = symbol;
    }
    public watchlist_items() {
        // Default constructor
    }
    public watchlist_items(String itemId, String symbol, watchlist watchlist) {
        this.itemId = itemId;
        this.symbol = symbol;
        this.watchlist = watchlist;
    }
}
