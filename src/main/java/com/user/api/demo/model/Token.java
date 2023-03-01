package com.user.api.demo.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "tokens")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_value")
    private String value;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Timestamp expirationDate;

    public Token() {
    }

    public Token(Long id, String value, User user, Timestamp expirationDate) {
        this.id = id;
        this.value = value;
        this.user = user;
        this.expirationDate = expirationDate;
    }



    // getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Timestamp getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Timestamp expirationDate) {
        this.expirationDate = expirationDate;
    }
}
