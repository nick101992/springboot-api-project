package com.user.api.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.user.api.demo.model.UserRole;
import com.user.api.demo.repo.UserRoleRepository;

@Service
public class UserRoleService {

	@Autowired
	UserRoleRepository repo;

    public Optional<UserRole> findByUserIdAndRoleId(int user_id , int role_id) {
        return repo.findByUserIdAndRoleId(user_id,role_id);
    }
    
    public UserRole save(UserRole userRole) {
        return repo.save(userRole);
    }

}
