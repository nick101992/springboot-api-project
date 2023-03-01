package com.user.api.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;



@Entity
@Table(name="user_data")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int user_id;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AccessAttempt> accessAttempts;
	
	@Column
	private String username;
	
	@Column
	private String password;
	    
	@Column

	private String saltKey;
	
	@Column
	private int numAttempts;
	
	@Column
	private Boolean userActive;

	@OneToMany(mappedBy = "user")
	private List<Token> tokens;
	
	public User() {
		super();
		this.numAttempts = 0;
		this.userActive = true;
	}
	public Integer getId() {
		return user_id;
	}
	
	public void setId(int user_id) {
		this.user_id = user_id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getSaltKey() {
		return saltKey;
	}
	
	public void setSaltKey(String saltKey) {
		this.saltKey = saltKey;
	}
	
	public int getNumAttempts() {
		return numAttempts;
	}
	
	public void setNumAttempts(int numAttempts) {
		this.numAttempts = numAttempts;
	}
	
	public Boolean getUserActive() {
		return userActive;
	}
	
	public void setUserActive(Boolean userActive) {
		this.userActive = userActive;
	}
	
	public List<AccessAttempt> getAccessAttempts() {
        return accessAttempts;
    }

    public void setAccessAttempts(List<AccessAttempt> accessAttempts) {
        this.accessAttempts = accessAttempts;
    }

	public List<Token> getTokens() {
		return tokens;
	}

	public void setTokens(List<Token> tokens) {
		this.tokens = tokens;
	}

	@Override
	public String toString() {
		return "User [username=" + username + "]";
	}
	public boolean isPasswordValid(final String passwordPattern) {
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(this.password);
        return matcher.matches();
	}

}	
