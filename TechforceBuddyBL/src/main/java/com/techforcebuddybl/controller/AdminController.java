package com.techforcebuddybl.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techforcebuddybl.services.impl.ExtractDataFromPdfServiceImpl;
import com.techforcebuddybl.services.impl.Word2VecTraineFromJSON;
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
	 * for unstructured data
	 * and only admin can access this API.
	 */
	
	@GetMapping("/v1/preProcess")
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
	 * This is GET API for pre process the data of the pdf file 
	 * for structured data
	 * and only admin can access this API.
	 */
	@GetMapping("/v2/preProcess")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> processDataOfPDFToCreateJSon() {
		dataFromPdfServiceImpl.processPDFDataToCreateJson();
		return ResponseEntity.ok("PDF extraction successful");
	}
	
	/*
	 * This is GET API for training the modal using all raw data files
	 * for unstructured data 
	 * and only admin can access this API.
	 */
	@GetMapping("/v1/trainModal")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> trainModelVersionOne(){
		try {
			modelServiceImpl.trainModel();
			return ResponseEntity.ok("Model Trained");
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body("Error extracting PDF: " + e.getMessage());
		}
	}
	
	@GetMapping("/v2/trainModal")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> trainModelVersionTwo(){
		try {
			Word2VecTraineFromJSON.trainWordTwoVecJson();
			return ResponseEntity.ok("Model Trained");
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body("Error extracting PDF: " + e.getMessage());
		}
	}
}
