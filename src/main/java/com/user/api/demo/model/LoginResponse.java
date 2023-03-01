package com.user.api.demo.model;

public class LoginResponse {
    private final String token;
    private final String errorMessage;
    private final Integer userId;

    public LoginResponse(String token, String errorMessage, Integer userId) {
        this.token = token;
        this.errorMessage = errorMessage;
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Integer getUserId() {
        return userId;
    }
}
