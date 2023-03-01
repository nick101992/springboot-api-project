package com.user.api.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.user.api.demo.model.Roles;
import com.user.api.demo.repo.RoleRepository;

@Service
public class RoleService {

	@Autowired
	RoleRepository repo;

	public List<Roles> findAll() {
		return repo.findAll();
	}

    public Roles save(Roles role) {
        return repo.save(role);
    }

    public Optional<Roles> findById(Integer id) {
        return repo.findById(id);
    }

    public boolean checkRoleExists(Integer id) {
        return repo.existsById(id);
    }


    public boolean deleteRoleById(Integer id) {
        repo.deleteById(id);
        return true;
    }

    public String deleteAll() {
        repo.deleteAll();
        return "All records deleted";
    }
    
    public Optional<Roles> findByrolesName(String role) {
        return repo.findByrolesName(role);
    }

}