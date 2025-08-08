package com.quantify.quantify_backend.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="usertable")
public class user {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private String userId;

    @Column(name="google_id", unique = true)
    private String googleId;

    @Column(name = "username")
    private String username;

    @Column(name="password")
    private String password;

    @Column(name = "email")
    private String email;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="balance_id")
    private balance balance;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<holding> holdings;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<watchlist> watchlists;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public balance getBalance() {
        return balance;
    }

    public void setBalance(balance balance) {
        this.balance = balance;
    }

    public List<holding> getHoldings() {
        return holdings;
    }

    public void setHoldings(List<holding> holdings) {
        this.holdings = holdings;
    }

    public List<watchlist> getWatchlists() {
        return watchlists;
    }

    public void setWatchlists(List<watchlist> watchlists) {
        this.watchlists = watchlists;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    @Override
    public String toString() {
        return "user{" +
                "userId='" + userId + '\'' +
                ", googleId='" + googleId + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public user() {
    }

    public user(String userId, String username, String password, String email) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
    }




}
