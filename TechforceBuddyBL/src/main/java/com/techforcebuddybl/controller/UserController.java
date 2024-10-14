package com.techforcebuddybl.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.techforcebuddybl.dto.Question;
import com.techforcebuddybl.entity.UserEntity;
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
	

	/*
	 * Search query in unstructured data
	 */
	@PostMapping(path="/v1/query",consumes = "application/json")
	@CrossOrigin(origins = "http://localhost:8081")
	public ResponseEntity<?> processQueryForResponse(@RequestBody Question question ) {

		try {
			return new ResponseEntity<Map<String,String>>(
					dataProcessingServiceImpl.getResponsUsingUnstructuredData(
							question.getQuery()),HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.NOT_FOUND);
		}
		
	}
	
	
	/*
	 * Search the query in structured data
	 */
	@PostMapping(path="/v2/query",consumes = "application/json")
	@CrossOrigin(origins = "http://localhost:8081")
	public ResponseEntity<?> processQuery(@RequestBody Question question ) {

		try {
			return new ResponseEntity<Map<String,List<String>>>(
					dataProcessingServiceImpl.getResponsUsingStructuredData(
							question.getQuery()),HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.NOT_FOUND);
		}
		
	}
	
}
