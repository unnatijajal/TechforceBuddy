package com.authenticationservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authenticationservice.dto.LoginRequest;
import com.authenticationservice.entity.UserEntity;
import com.authenticationservice.jwt.JwtResponse;
import com.authenticationservice.jwt.JwtUtil;
import com.authenticationservice.services.CustomUserDetailsService;
import com.authenticationservice.services.impl.UserServiceImpl;

/*
 * This is the RestController class to create the Rest API
 * and this create the API for authenticate the user. 
 */

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

	@Autowired
	private AuthenticationManager authenticationManager ;

	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	UserServiceImpl impl;
	
	@Autowired
	CustomUserDetailsService customUserDetailsService;
	
	/*
	 * This is POST API for login process	 
	 */
	@PostMapping(path="/login", consumes="application/json")
	@CrossOrigin("*")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
		String email = loginRequest.getEmail().trim();
		String password = loginRequest.getPassword().trim();
		try {
			//Authenticate the user
			Authentication authentication = authenticationManager.authenticate(new
					UsernamePasswordAuthenticationToken(email, password));
			
			if(authentication.isAuthenticated()) {
				//Generate a token for the authenticated user
				String token = jwtUtil.generateToken(loginRequest.getEmail());
				
				//Return the token in the response
				return ResponseEntity.ok(new JwtResponse(token));
			}else {
				return new ResponseEntity<String>("Invalid User",HttpStatusCode.valueOf(401));
			}	
		}catch(Exception e) {
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	/*
	 * Create the POST API for saving the user's details into the DB.
	 */
	@PostMapping("/signin")
	@CrossOrigin(origins = "*")
	public ResponseEntity<?> signin(@RequestBody UserEntity entity) {
		if (impl.saveUser(entity) != null) {
			return new ResponseEntity<String>("Saved", HttpStatus.OK);
		} else {
			return new ResponseEntity<String>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
}