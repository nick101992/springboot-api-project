package com.user.api.demo.controller;

import java.util.*;

import com.user.api.demo.model.*;
import com.user.api.demo.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.MDC;

@RestController
public class UserController {

	@Autowired
	UserService userService;
	@Autowired
	RoleService roleService;
	@Autowired
	UserRoleService userRoleService;
	@Autowired
	AccessAttemptService accessAttemptService;
	@Autowired
	private LoggerService loggerService;

	@Value("${password.regex}")
    private String passwordRegex;


	// List of user access attempts
	// http://localhost:8080/access-attempts/{userId}
	@GetMapping("/access-attempts/{userId}")
	public List<AccessAttemptsReport> getAccessAttemptsForUser(@PathVariable Integer userId) {
		Optional<User> userOptional = userService.findById(userId);
		User user = userOptional.get();
		return accessAttemptService.findByUserId(user.getId());
	}


	// Get all users
	// http://localhost:8080/users

	@GetMapping("/users")
	public List<UserReport> getAllUsers() {
		return userService.findSelectedUsers();
	}
	//Returns the HTML file name
	@GetMapping("/login")
	public String loginPage() {
	    return "login";
	}
	

	// Add user with role
	// http://localhost:8080/users-role
	@PostMapping("/users-role")
	public ResponseEntity<?> addUser(@RequestBody UserRoleRequest userRoleRequest ) {

		Optional<User> user = userService.findByUsername(userRoleRequest.getUsername());
		Optional<Roles> role = roleService.findByrolesName(userRoleRequest.getRoleName());
		
		if (user.isPresent() && role.isPresent()) {
			
			int user_id = user.get().getId();
			int role_id = role.get().getId();
			
			Optional<UserRole> userRolePresence = userRoleService.findByUserIdAndRoleId(user_id, role_id);
			
			if (userRolePresence.isPresent()) {
				return new ResponseEntity<String>("User " + userRoleRequest.getUsername() + " with this role " 
				+ userRoleRequest.getRoleName() + "is already present", HttpStatus.CONFLICT);
			}
			
			UserRole userRoleAssociation = new UserRole();
			User userObj = user.get();
			Roles roleObj = role.get();
			userRoleAssociation.setUser(userObj);
			userRoleAssociation.setRole(roleObj);
			UserRole newUserRoleAssociation = userRoleService.save(userRoleAssociation);
			return new ResponseEntity<UserRole>(newUserRoleAssociation, HttpStatus.CREATED);
		}

		return new ResponseEntity<String>("User " + userRoleRequest.getUsername() + " or Role " + userRoleRequest.getRoleName() 
		+ " is not present in database. You must first generate the user or role you entered. "
		+ "Use another REST service", HttpStatus.CONFLICT);
		
	}
	
	// Add user
	// http://localhost:8080/users
	@PostMapping("/users")
	public ResponseEntity<?> addUser(@RequestBody User user ) {
		
		String transactionId = UUID.randomUUID().toString();
		MDC.put("transactionId", transactionId);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Transaction-ID", transactionId);
	
		Optional<User> finduser = userService.findByUsername(user.getUsername());
				
		if (finduser.isPresent()) {
			MDC.remove("transactionId");
			return new ResponseEntity<String>("User " + user.getUsername() + " already present", headers, HttpStatus.CONFLICT);
		}
			if (user.isPasswordValid(passwordRegex)) {
				User newUser = userService.save(user);
				UserReport response = new UserReport();
				response.setUsername(newUser.getUsername());
				response.setNumAttempts(newUser.getNumAttempts());
				response.setUserActive(newUser.getUserActive());
				MDC.remove("transactionId");
				return new ResponseEntity<UserReport>(response, headers, HttpStatus.CREATED);
			}else {
				MDC.remove("transactionId");
				return new ResponseEntity<String>("Password Inserita non Valida", headers, HttpStatus.UNPROCESSABLE_ENTITY);
			}
	}

	// GET Single user
	// http://localhost:8080/users/1
	@GetMapping(path = "/users/{id}")
	public ResponseEntity<?> getUser(@PathVariable("id") Integer id) {
		Optional<User> user = userService.findById(id);
		if (!user.isPresent()) {
			return new ResponseEntity<String>("User " + id + " not found.",HttpStatus.NOT_FOUND);
		}

		UserReport response = new UserReport();
		response.setUsername(user.get().getUsername());
		response.setNumAttempts(user.get().getNumAttempts());
		response.setUserActive(user.get().getUserActive());

		return new ResponseEntity<UserReport>(response, HttpStatus.FOUND);
	}

	// Delete Single users
	// http://localhost:8080/users/1
	@DeleteMapping(path = "/users/{id}")
	public ResponseEntity<String> deleteUser(@PathVariable("id") Integer id) {
		boolean isUserExists = userService.checkUserExists(id);

		if (!isUserExists) {
			return new ResponseEntity<String>("User " + id + " not found.", HttpStatus.NOT_FOUND);
		} else {
			userService.deleteUserById(id);
		}
		return new ResponseEntity<String>("User " + id + " deleted successfully", HttpStatus.OK);
		}
	
	// Delete All users
	// http://localhost:8080/users
	@DeleteMapping("/users")
	public String deleteAll() {
		return userService.deleteAll();
	}

}
