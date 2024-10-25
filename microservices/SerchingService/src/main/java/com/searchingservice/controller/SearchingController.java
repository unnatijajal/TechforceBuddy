package com.searchingservice.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.searchingservice.service.impl.SearchingKeywordsServiceImpl;

@RestController
public class SearchingController {
	
	@Autowired
	private SearchingKeywordsServiceImpl searchingKeywordsServiceImpl;

	@GetMapping("/v1/query")
	public ResponseEntity<?> getRelevantContentUsingUnstructuredData(@RequestParam String query){
		try {
			return new ResponseEntity<Map<String, String>>(searchingKeywordsServiceImpl.searchingKeywordsForUnstructuredData(query), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}
	
}
