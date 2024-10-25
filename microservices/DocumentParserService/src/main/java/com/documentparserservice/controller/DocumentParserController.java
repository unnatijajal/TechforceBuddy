package com.documentparserservice.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.documentparserservice.services.impl.FetchFileContentServiceImpl;
import com.documentparserservice.services.impl.GenerateFileServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;

@RestController

public class DocumentParserController {

	@Autowired
	private GenerateFileServiceImpl fileServiceImpl;
	
	@Autowired
	private FetchFileContentServiceImpl fetchFileContentServiceImpl;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@GetMapping("/create/textFiles")
	@CrossOrigin(origins = "http://localhost:8080")
	public ResponseEntity<?> createTextFile() throws IOException {
		LinkedHashMap<String,String> contentWithFileName = restTemplate.getForObject("http://localhost:8081/pdfExtraction/getContent", LinkedHashMap.class);
		
		contentWithFileName.entrySet()
				.stream()
				.forEach(entry -> {
					try {
						fileServiceImpl.createTextFile(entry.getValue(), entry.getKey());
					} catch (IOException e) {
						e.printStackTrace();
					}
				});	
		return new ResponseEntity<String>("File created successfully",HttpStatus.OK);
	}
	
	@GetMapping("/create/jsonFile")
	@CrossOrigin(origins = "http://localhost:8080")
	public ResponseEntity<?> createJsonFile(){
		LinkedHashMap<String,String> contentWithFileName = restTemplate.getForObject("http://localhost:8081/pdfExtraction/getContent", LinkedHashMap.class);
		try {
			fileServiceImpl.createJsonFile(contentWithFileName);
			return new ResponseEntity<String>("Json file created",HttpStatus.OK); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.OK); 
		}
	}
	
	@CrossOrigin(origins = "http://localhost:8080")
	@GetMapping("/getTextFileContent")
	public ResponseEntity<?> getContentOfTextFiles(){
		try {
			return new ResponseEntity<List<String>>(fetchFileContentServiceImpl.getTextFileContent(),HttpStatus.OK);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@CrossOrigin("http://localhost:8080")
	@GetMapping("/getJsonFileContent")
	public ResponseEntity<?> getContentOfJsonFile(){
		try {
			return new ResponseEntity<JsonNode>(fetchFileContentServiceImpl.getJSonFileContent(),HttpStatus.OK);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
