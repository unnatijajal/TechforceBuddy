package com.techforcebuddybl.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techforcebuddybl.services.impl.ExtractDataFromPdfServiceImpl;
import com.techforcebuddybl.services.impl.WordTwoVectorModelServiceImpl;

@RestController
public class AdminController {

	@Autowired
	private ExtractDataFromPdfServiceImpl dataFromPdfServiceImpl;
	
	@Autowired
	private WordTwoVectorModelServiceImpl modelServiceImpl;
	
	@GetMapping("/preProcess")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> extractPdf() {
		try {
			dataFromPdfServiceImpl.accessFiles();
			return ResponseEntity.ok("PDF extraction successful");
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body("Error extracting PDF: " + e.getMessage());
		}
	}
	
	@GetMapping("/trainModal")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> trainModel(){
		try {
			modelServiceImpl.trainModel();
			return ResponseEntity.ok("Model Trained");
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body("Error extracting PDF: " + e.getMessage());
		}
	}
}
