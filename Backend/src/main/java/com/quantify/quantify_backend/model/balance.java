package com.quantify.quantify_backend.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="balance")
public class balance {

    @Id
    @Column(name = "balance_id")
    private String balanceId;

    @Column(name="balance")
    private int balance;

    @OneToOne(mappedBy = "balance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private user user;

    @OneToMany(mappedBy = "balance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<transaction> transactions;

    public String getBalanceId() {
        return balanceId;
    }
    public void setBalanceId(String balanceId) {
        this.balanceId = balanceId;
    }

    public int getBalance() {
        return balance;
    }
    public void setBalance(int balance) {
        this.balance = balance;
    }

    public user getUser() {
        return user;
    }
    public void setUser(user user) {
        this.user = user;
    }
    public List<transaction> getTransactions() {
        return transactions;
    }
    public void setTransactions(List<transaction> transactions) {
        this.transactions = transactions;
    }
    public balance() {
    }

    public balance(int balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "balance{" +
                "balanceId='" + balanceId + '\'' +
                ", balance='" + balance + '\'' +
                '}';
    }
}
