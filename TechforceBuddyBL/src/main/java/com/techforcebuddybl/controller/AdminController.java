package com.techforcebuddybl.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techforcebuddybl.services.impl.ExtractDataFromPdfServiceImpl;
import com.techforcebuddybl.services.impl.WordTwoVectorModelServiceImpl;
/*
 * This is the controller class for Admin
 */
@RestController
public class AdminController {

	@Autowired
	private ExtractDataFromPdfServiceImpl dataFromPdfServiceImpl;
	
	@Autowired
	private WordTwoVectorModelServiceImpl modelServiceImpl;
	
	/*
	 * This is GET API for pre process the data of the pdf file 
	 * and only admin can access this API.
	 */
	
	@GetMapping("/preProcess")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> processDataOfPDF() {
		try {
			dataFromPdfServiceImpl.processDataOfPDF();
			return ResponseEntity.ok("PDF extraction successful");
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body("Error extracting PDF: " + e.getMessage());
		}
	}
	
	/*
	 * This is GET API for training the modal using all raw data files 
	 * and only admin can access this API.
	 */
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
