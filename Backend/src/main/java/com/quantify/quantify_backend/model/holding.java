package com.quantify.quantify_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name="holding")
public class holding {

    @Id
    @Column(name="holding_id")
    private String holdingId;


    @Column(name="symbol")
    private String symbol;

    @Column(name="quantity")
    private int quantity;

    @Column(name="average_price")
    private double averagePrice;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private user user;

    public holding() {
    }

    public holding(String symbol, int quantity, double averagePrice) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.averagePrice = averagePrice;
    }

    public String getHoldingId() {
        return holdingId;
    }

    public void setHoldingId(String holdingId) {
        this.holdingId = holdingId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(double averagePrice) {
        this.averagePrice = averagePrice;
    }

    public user getUser() {
        return user;
    }

    public void setUser(user user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "holding{" +
                "holdingId='" + holdingId + '\'' +
                ", symbol='" + symbol + '\'' +
                ", quantity=" + quantity +
                ", averagePrice=" + averagePrice +
                '}';
    }
}
