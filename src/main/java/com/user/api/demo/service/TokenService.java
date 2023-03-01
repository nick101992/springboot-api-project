package com.user.api.demo.service;

import com.user.api.demo.model.Token;
import com.user.api.demo.model.User;
import com.user.api.demo.repo.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Optional;

@Service
public class TokenService {

    @Autowired
    TokenRepository repo;

    public Token save(Token token) {
        return repo.save(token);
    }

    public String generateToken() {
        String jwt = Jwts.builder()
                .setSubject("user")
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                .signWith(SignatureAlgorithm.HS256, "secretKey")
                .compact();

        return jwt;
    }

    public Token findByValue(String tokenValue) {
        return repo.findByValue(tokenValue);
    }

}