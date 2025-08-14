package com.quantify.quantify_backend.model;

import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name="watchlist")
public class watchlist {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "watchlist_id")
    private String watchlistId;

    @Column(name = "name")
    private String name;

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST,CascadeType.DETACH,CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private user user;

    @OneToMany(mappedBy = "watchlist", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private List<watchlist_items> items;

    public String getWatchlistId() {
        return watchlistId;
    }

    public void setWatchlistId(String watchlistId) {
        this.watchlistId = watchlistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public user getUser() {
        return user;
    }

    public void setUser(user user) {
        this.user = user;
    }

    public List<watchlist_items> getItems() {
        return items;
    }

    public void setItems(List<watchlist_items> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "watchlist{" +
                "watchlistId='" + watchlistId + '\'' +
                ", name='" + name + '\'' +
                ", user=" + user +
                '}';
    }

    public watchlist() {
    }

    public watchlist(String name, user user) {
        this.name = name;
        this.user = user;
    }
}
