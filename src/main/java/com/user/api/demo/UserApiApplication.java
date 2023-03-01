package com.user.api.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class UserApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserApiApplication.class, args);
	}


	
	
/** _endpoints
GET ALL RESOURCES
http://localhost:8080/users

GET SINGLE RESOURCE
http://localhost:8080/users/{id}

POST CREATE NEW RESOURCE 
http://localhost:8080/users

PUT - Update existing resource or create new if does not exist 
http://localhost:8080/users/{id}

PATCH PARTIAL UPDATE 
http://localhost:8080/users/{id}

DELETE SINGLE RESOURCE
http://localhost:8080/users/{id}

DELETE ALL RESOURCE
http://localhost:8080/users/
**/
	
}
