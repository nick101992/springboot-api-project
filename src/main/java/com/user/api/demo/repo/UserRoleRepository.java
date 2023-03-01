package com.user.api.demo.repo;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.user.api.demo.model.UserRole;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Integer>{

	@Query("SELECT ur FROM UserRole ur WHERE ur.user.id = :user_id AND ur.role.id = :role_id")
	Optional<UserRole> findByUserIdAndRoleId(@Param("user_id") int user_id, @Param("role_id") int roleId);

}
