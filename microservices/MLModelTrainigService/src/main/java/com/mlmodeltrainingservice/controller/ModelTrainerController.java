package com.mlmodeltrainingservice.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.mlmodeltrainingservice.dto.Question;
import com.mlmodeltrainingservice.service.impl.PythonAPICallerServiceImpl;
import com.mlmodeltrainingservice.service.impl.SearchingKeywordsStructuredDataServiceImpl;
import com.mlmodeltrainingservice.service.impl.SearchingKeywordsUnstructuredDataServiceImpl;
import com.mlmodeltrainingservice.service.impl.TrainModelServiceImpl;

@RestController
public class ModelTrainerController {

	@Autowired
	private TrainModelServiceImpl trainModelServiceImpl;
	
	@Autowired
	private SearchingKeywordsStructuredDataServiceImpl keywordsStructuredDataServiceImpl;
	
	@Autowired
	private SearchingKeywordsUnstructuredDataServiceImpl keywordsServiceImpl;
	
	@Autowired
	private RestTemplate restTemplate;
	
	
	@Autowired
	private PythonAPICallerServiceImpl apiCallerServiceImpl;
	
	@CrossOrigin(origins = "*")
	@GetMapping("/v1/trainModel")
	public ResponseEntity<String> trainModelUsingTextFile(){
		List<String> sentences = restTemplate.getForObject("http://192.168.1.214:8082/getTextFileContent", List.class);
		trainModelServiceImpl.trainModelUsingTextFileContent(sentences);
		return new ResponseEntity<String>("Model trained..",HttpStatus.OK);
	}
	
	@CrossOrigin(origins = "*")
	@GetMapping("/v2/trainModel")
	public ResponseEntity<String> trainModelUsingJsonFile(){
		JsonNode rootNode= restTemplate.getForObject("http://192.168.1.214:8082/getJsonFileContent", JsonNode.class);
		trainModelServiceImpl.trainModelUSingJSonFileContent(rootNode);
		return new ResponseEntity<String>("Model trained..",HttpStatus.OK);
	}
	
	/*
	 * Search query in unstructured data
	 * and generate the summary
	 */
	@PostMapping("/v1/query-and-summary")
	@CrossOrigin(origins = "*")
	public ResponseEntity<Map<String, Object>> getResponseAndSummaryUnstructuredData(@RequestBody Question question) {
	    try {
	        // Retrieve structured content
	        LinkedHashMap<String, String> content = keywordsServiceImpl.getResponsUsingUnstructuredData(question.getQuery());
			StringBuilder inputText = new StringBuilder();
			content.keySet().stream()
					.forEach(value -> inputText.append(value));
	        String summary = apiCallerServiceImpl.callGenerateSummaryApi(inputText.toString());

	        // Create response map
	        Map<String, Object> response = new HashMap<>();
	        response.put("summary", summary);
	        response.put("unstructuredContent", content);

	        // Return both summary and detailed response
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    } catch (Exception e) {
	        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
	    }
	}
	
	
	/*
	 * Search the query in structured data
	 * and generate the summary
	 */
	@PostMapping("/v2/query-and-summary")
	@CrossOrigin(origins = "*")
	public ResponseEntity<Map<String, Object>> getResponseAndSummaryStructuredData(@RequestBody Question question) {
	    try {
	        // Retrieve structured content
	        LinkedHashMap<String, List<String>> content = keywordsStructuredDataServiceImpl.getResponsUsingStructuredData(question.getQuery());

	        // Generate summary from structured content
	        StringBuilder inputText = new StringBuilder();
	        content.values().forEach(list -> list.forEach(inputText::append));
	        String summary = apiCallerServiceImpl.callGenerateSummaryApi(inputText.toString());

	        // Create response map
	        Map<String, Object> response = new HashMap<>();
	        response.put("summary", summary);
	        response.put("structuredContent", content);

	        // Return both summary and detailed response
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    } catch (Exception e) {
	        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
	    }
	}


}
