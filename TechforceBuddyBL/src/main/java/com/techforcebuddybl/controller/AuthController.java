package com.techforcebuddybl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techforcebuddybl.jwt.JwtResponse;
import com.techforcebuddybl.jwt.JwtUtil;

/*
 * This is the RestController class to create the Rest API
 * and this create the API for authenticate the user. 
 */

@RestController
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager ;

	@Autowired
	private JwtUtil jwtUtil;
	
	/*
	 * This is POST API for login process	 
	 */
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestParam("email") String email, @RequestParam("password") String password ) {
		//Authenticate the user
		Authentication authentication = authenticationManager.authenticate(new
				UsernamePasswordAuthenticationToken(email, password));
		
		if(authentication.isAuthenticated()) {
			//Generate a token for the authenticated user
			String token = jwtUtil.generateToken(email);
			System.out.println(token);
			//Return the token in the response
			return ResponseEntity.ok(new JwtResponse(token));
		}else {
			return new ResponseEntity<String>("Invalid User",HttpStatusCode.valueOf(401));
		}
	}
}
