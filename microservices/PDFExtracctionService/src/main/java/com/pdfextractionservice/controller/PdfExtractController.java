package com.pdfextractionservice.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pdfextractionservice.service.impl.PdfExtractionServiceImpl;

/*
 * This is the controller class for PDF Extraction service
 */

@RestController
@RequestMapping("/pdfExtraction")
public class PdfExtractController {

	@Autowired
	private PdfExtractionServiceImpl pdfExtractionServiceImpl;
	
	/*
	 * This is GET API for get the content of the pdf file 
	 */
	@GetMapping("/getContent")
	public ResponseEntity<?> getContentOfPdf(){
		try {
			return new ResponseEntity<LinkedHashMap<String,String>>(pdfExtractionServiceImpl.getContentFromPdf(),HttpStatus.FOUND);
		}catch(IOException e) {
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.NOT_FOUND);
		}
	}
	
	@CrossOrigin(origins = "http://localhost:8080")
	@GetMapping(value = "/download/{filename}", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<InputStreamResource> getPdf(@PathVariable String filename) throws IOException {
		File pdfFile = new File(System.getProperty("user.dir") + "/src/main/resources/pdf/" + filename);
		InputStreamResource resource = new InputStreamResource(new FileInputStream(pdfFile));
		if (resource.exists()) {
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename)
					.contentType(MediaType.APPLICATION_PDF).body(resource);
		} else {
			return ResponseEntity.notFound().build();
		}

	}
	
}
