package com.user.api.demo.model;


public class UserRoleRequest {
	private String username;
	private String password;
	private String roleName;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@Override
	public String toString() {
		return "UserRoleRequest [username=" + username + ", password=" + password + ", roleName=" + roleName + "]";
	}	
}
