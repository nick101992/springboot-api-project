package com.user.api.demo.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.user.api.demo.enumerations.AccessResult;

@Entity
@Table(name="access_attempts")
public class AccessAttempt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column
    private LocalDateTime timestamp;
    
    @Enumerated(EnumType.STRING)
    @Column
    private AccessResult accessResult;
    
    @ManyToOne
    private User user;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime localDateTime) {
        this.timestamp = localDateTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
	public AccessResult getAccessResult() {
		return accessResult;
	}
	public void setAccessResult(AccessResult accessResult) {
		this.accessResult = accessResult;
	}
	
	@Override
	public String toString() {
		return "AccessAttempt [id=" + id + ", timestamp=" + timestamp + ", accessResult=" + accessResult + ", user="
				+ user + "]";
	}

}
