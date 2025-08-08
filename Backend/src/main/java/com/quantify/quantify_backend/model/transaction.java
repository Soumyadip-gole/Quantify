package com.quantify.quantify_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name="transaction")
public class transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="transaction_id")
    private String transactionId;

    @Column(name="symbol")
    private String symbol;

    @Column(name="quantity")
    private int quantity;

    @Column(name="price")
    private double price;

    @Column(name="transaction_type")
    private String transactionType;

    @Column(name="transaction_date")
    private String transactionDate;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name="balance_id")
    private balance balance;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public transaction() {
    }

    public transaction(String symbol, int quantity, double price, String transactionType, String transactionDate) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;
    }

    @JsonIgnore
    public balance getBalance() {
        return balance;
    }

    public void setBalance(balance balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", symbol='" + symbol + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", transactionType='" + transactionType + '\'' +
                ", transactionDate='" + transactionDate + '\'' +
                '}';
    }
}
