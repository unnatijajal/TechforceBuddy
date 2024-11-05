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

import com.documentparserservice.feign.PdfExtraction;
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
	private PdfExtraction extraction;
	
	@GetMapping("/v1/preProcess")
	//@CrossOrigin(origins = "*")
	public ResponseEntity<?> createTextFile() throws IOException {
		LinkedHashMap<String,String> contentWithFileName = (LinkedHashMap<String, String>) extraction.getContentOfPdf().getBody();
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
	
	@GetMapping("/v2/preProcess")
	//@CrossOrigin(origins = "*")
	public ResponseEntity<?> createJsonFile(){
		LinkedHashMap<String,String> contentWithFileName = (LinkedHashMap<String, String>) extraction.getContentOfPdf().getBody();
		try {
			fileServiceImpl.createJsonFile(contentWithFileName);
			return new ResponseEntity<String>("Json file created",HttpStatus.OK); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.OK); 
		}
	}
	
	//@CrossOrigin(origins = "*")
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
	
	//@CrossOrigin(origins = "*")
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
