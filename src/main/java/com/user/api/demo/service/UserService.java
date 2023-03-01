package com.user.api.demo.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.user.api.demo.model.UserReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.user.api.demo.model.User;
import com.user.api.demo.repo.UserRepository;

@Service
public class UserService {

	@Autowired
	UserRepository repo;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
 
	public List<User> findAll() {
		return repo.findAll();
	}

    public User save(User user) {
    	String salt = UUID.randomUUID().toString();
    	user.setSaltKey(salt);
        user.setPassword(passwordEncoder.encode(user.getPassword()+salt));
        return repo.save(user);
    }

    public Optional<User> findById(Integer id) {
        return repo.findById(id);
    }

    public boolean checkUserExists(Integer id) {
        return repo.existsById(id);
    }

    public User updateUser(User user) {
        user.setUsername(user.getUsername());
        user.setNumAttempts(user.getNumAttempts());
        user.setUserActive(user.getUserActive());
        return repo.save(user);
    }

    public boolean deleteUserById(Integer id) {
        repo.deleteById(id);
        return true;
    }

    public String deleteAll() {
        repo.deleteAll();
        return "All records deleted";
    }

    public Optional<User> findByUsername(String username) {
        return repo.findByUsername(username);
    }

    public void updateUserAttempts(User user, int numAttempts){
        user.setNumAttempts(numAttempts);
        updateUser(user);
    }
    public void updateUserStatus(User user, boolean userActive){
        user.setUserActive(userActive);
        updateUser(user);
    }

    public List<UserReport> findSelectedUsers() {
        return repo.findSelectedUsers();
    }

    public User findByTokenAndId(String token, Integer id) {
        return repo.findByTokenAndId(token,id);
    }

}


