package com.user.api.demo.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.user.api.demo.model.Roles;

@Repository
public interface RoleRepository extends JpaRepository<Roles, Integer>{

	Optional<Roles> findByrolesName(String username);
}

