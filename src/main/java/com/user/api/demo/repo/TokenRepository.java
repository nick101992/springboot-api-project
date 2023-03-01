package com.user.api.demo.repo;
import com.user.api.demo.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {

    Token findByValue(String tokenValue);

}