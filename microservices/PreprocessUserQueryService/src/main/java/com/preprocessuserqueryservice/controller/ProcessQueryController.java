package com.preprocessuserqueryservice.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.preprocessuserqueryservice.service.impl.ProcessQueryServiceImpl;

@RestController

public class ProcessQueryController {

	@Autowired
	private ProcessQueryServiceImpl queryServiceImpl;
	
	@PostMapping("/processQuery")
	@CrossOrigin(origins = "*")
	public ResponseEntity<?> getQuerKeywords(@RequestParam String query){
		try {
			return new ResponseEntity<List<String>>(queryServiceImpl.getQueryKeywords(query),HttpStatus.OK);	 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<String>("Something went wrong",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
}
