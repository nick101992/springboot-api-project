package com.user.api.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.Id;

import javax.persistence.Table;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;



@Entity
@Table(name="roles_data")
public class Roles {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int role_id;
	
	@Column
	private String rolesName;
	
	public Roles() {
		super();
	}
	public Integer getId() {
		return role_id;
	}
	public void setId(int role_id) {
		this.role_id = role_id;
	}
	public String getRolesName() {
		return rolesName;
	}
	public void setRolesName(String rolesName) {
		this.rolesName = rolesName;
	}

}