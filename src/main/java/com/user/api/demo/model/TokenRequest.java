package com.user.api.demo.model;

import java.sql.Timestamp;

public class TokenRequest {
    private String value;
    private Integer user;
    private Timestamp expirationDate;

    public TokenRequest() {
    }

    public TokenRequest(String value, Integer user, Timestamp expirationDate) {
        this.value = value;
        this.user = user;
        this.expirationDate = expirationDate;
    }

    public String getValue() {
        return value;
    }

    public Integer getUser() {
        return user;
    }

    public Timestamp getExpirationDate() {
        return expirationDate;
    }
}
