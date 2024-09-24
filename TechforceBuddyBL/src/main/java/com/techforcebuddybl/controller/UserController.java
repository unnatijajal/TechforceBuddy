package com.techforcebuddybl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.techforcebuddybl.dto.Question;
import com.techforcebuddybl.entity.UserEntity;
import com.techforcebuddybl.jwt.JwtResponse;
import com.techforcebuddybl.services.impl.UserDataProcessingServiceImpl;
import com.techforcebuddybl.services.impl.UserServiceImpl;
/*
 * Controller class for use's related API
 */

@RestController
public class UserController {

	@Autowired
	private UserServiceImpl userServiceImpl;
	
	@Autowired
	private UserDataProcessingServiceImpl dataProcessingServiceImpl;
	
	/*
	 * Create the POST API for saving the user's details into the DB.
	 */
	@PostMapping("/signin")
	@CrossOrigin(origins = "http://localhost:8081")
	public ResponseEntity<?> signin(@RequestBody UserEntity entity) {
		System.out.println(entity);
		if(userServiceImpl.saveUser(entity)!=null) {
			return new ResponseEntity<String>("Saved",HttpStatus.OK);
		}else {
			return new ResponseEntity<String>("Something went wrong",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	

	@PostMapping(path="/query",consumes = "application/json")
	@CrossOrigin(origins = "http://localhost:8081")
	@PreAuthorize("hasRole('ROLE_USER')")
	public ResponseEntity<?> message(@RequestBody Question question ) {

		try {
			dataProcessingServiceImpl.divideSentenceIntoWords(question.getQuery());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.getStackTrace();
		}
		
		return ResponseEntity.ok(new JwtResponse(question.getQuery()));
	}
}
