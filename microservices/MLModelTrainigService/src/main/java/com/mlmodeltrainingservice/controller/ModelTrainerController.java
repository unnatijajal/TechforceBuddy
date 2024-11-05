package com.mlmodeltrainingservice.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.mlmodeltrainingservice.dto.Question;
import com.mlmodeltrainingservice.feign.GetRawData;
import com.mlmodeltrainingservice.feign.GetSummaryOfResponse;
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
	private GetRawData getRawData;
	
	@Autowired
	private GetSummaryOfResponse getSummaryOfResponse;
	
	
	/*
	 * @Autowired private PythonAPICallerServiceImpl apiCallerServiceImpl;
	 */
	
	//@CrossOrigin(origins = "*")
	@GetMapping("/v1/trainModel")
	public ResponseEntity<String> trainModelUsingTextFile(){
		List<String> sentences = (List<String>) getRawData.getContentOfTextFiles().getBody();
		trainModelServiceImpl.trainModelUsingTextFileContent(sentences);
		return new ResponseEntity<String>("Model trained..",HttpStatus.OK);
	}
	
	//@CrossOrigin(origins = "*")
	@GetMapping("/v2/trainModel")
	
	public ResponseEntity<String> trainModelUsingJsonFile(){
		JsonNode rootNode = getRawData.getContentOfJsonFile().getBody();
		trainModelServiceImpl.trainModelUSingJSonFileContent(rootNode);
		return new ResponseEntity<String>("Model trained..",HttpStatus.OK);
	}
	
	/*
	 * Search query in unstructured data
	 * and generate the summary
	 */
	@PostMapping("/v1/query-and-summary")
	public ResponseEntity<Map<String, Object>> getResponseAndSummaryUnstructuredData(@RequestBody Question question) {
	    try {
	        // Retrieve structured content
	        LinkedHashMap<String, String> content = keywordsServiceImpl.getResponsUsingUnstructuredData(question.getQuery());
			StringBuilder inputText = new StringBuilder();
			content.keySet().stream()
					.forEach(value -> inputText.append(value));
			Map<String, String> requestBodyForSummary = Map.of("input_text",inputText.toString());
			Map<String,Object> summaryResponse = getSummaryOfResponse.generateSummary(requestBodyForSummary);
	     // Get the generated_text which is expected to be a List
	        List<Map<String, String>> generatedTextList = (List<Map<String, String>>) summaryResponse.get("generated_text");
	     // Check if the list is not empty
	        String summary = "";
	        if (generatedTextList != null && !generatedTextList.isEmpty()) {
	            // Get the first item from the list
	            Map<String, String> firstItem = generatedTextList.get(0);
	            // Extract the summary_text value
	            summary = firstItem.get("summary_text");
	        }
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
	public ResponseEntity<Map<String, Object>> getResponseAndSummaryStructuredData(@RequestBody Question question) {
	    try {
	        // Retrieve structured content
	        LinkedHashMap<String, List<String>> content = keywordsStructuredDataServiceImpl.getResponsUsingStructuredData(question.getQuery());

	        // Generate summary from structured content
	        StringBuilder inputText = new StringBuilder();
	        content.values().forEach(list -> list.forEach(inputText::append));
	        Map<String, String> requestBodyForSummary = Map.of("input_text",inputText.toString());
			Map<String,Object> summaryResponse = getSummaryOfResponse.generateSummary(requestBodyForSummary);
			 // Get the generated_text which is expected to be a List
	        List<Map<String, String>> generatedTextList = (List<Map<String, String>>) summaryResponse.get("generated_text");
	     // Check if the list is not empty
	        String summary = "";
	        if (generatedTextList != null && !generatedTextList.isEmpty()) {
	            // Get the first item from the list
	            Map<String, String> firstItem = generatedTextList.get(0);
	            // Extract the summary_text value
	            summary = firstItem.get("summary_text");
	        }
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
