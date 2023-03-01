package com.user.api.demo.repo;

import java.util.List;
import java.util.Optional;

import com.user.api.demo.model.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.user.api.demo.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{
	
	Optional<User> findByUsername(String username);

	@Query("SELECT new com.user.api.demo.model.UserReport(u.username, u.numAttempts, u.userActive) FROM User u")
	List<UserReport> findSelectedUsers();

	@Query("SELECT u FROM User u JOIN u.tokens t WHERE t.value = :token AND u.id = :userId")
	User findByTokenAndId(@Param("token") String token, @Param("userId") Integer userId);
}
