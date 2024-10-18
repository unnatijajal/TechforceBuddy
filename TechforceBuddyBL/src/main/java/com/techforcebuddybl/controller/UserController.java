package com.techforcebuddybl.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techforcebuddybl.dto.Question;
import com.techforcebuddybl.entity.UserEntity;
import com.techforcebuddybl.services.impl.UserDataProcessingServiceImpl;
import com.techforcebuddybl.services.impl.UserServiceImpl;

/*
 * Controller class for use's related API
 */
@RestController
public class UserController {

	@Autowired
	private UserServiceImpl userServiceImpl;

	@Autowired
	private UserDataProcessingServiceImpl dataProcessingServiceImpl;

	/*
	 * Create the POST API for saving the user's details into the DB.
	 */
	@PostMapping("/signin")
	@CrossOrigin(origins = "http://localhost:8081")
	public ResponseEntity<?> signin(@RequestBody UserEntity entity) {
		System.out.println(entity);
		if (userServiceImpl.saveUser(entity) != null) {
			return new ResponseEntity<String>("Saved", HttpStatus.OK);
		} else {
			return new ResponseEntity<String>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/*
	 * Search query in unstructured data
	 */
	@PostMapping(path = "/v1/query", consumes = "application/json")
	@CrossOrigin(origins = "http://localhost:8081")
	public ResponseEntity<?> processQueryForResponse(@RequestBody Question question) {

		try {
			return new ResponseEntity<Map<String, String>>(
					dataProcessingServiceImpl.getResponsUsingUnstructuredData(question.getQuery()), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		}

	}

	/*
	 * Search the query in structured data
	 */
	@PostMapping(path = "/v2/query", consumes = "application/json")
	@CrossOrigin(origins = "http://localhost:8081")
	public ResponseEntity<?> processQuery(@RequestBody Question question) {

		try {
			return new ResponseEntity<LinkedHashMap<String, List<String>>>(
					dataProcessingServiceImpl.getResponsUsingStructuredData(question.getQuery()), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		}

	}

	@GetMapping("/openPdf")
	@CrossOrigin(origins = "http://localhost:8081")
	public ResponseEntity<Resource> downloadFile(@RequestParam String fileName) throws IOException {
		// Load file from the resources folder
		Resource resource = new ClassPathResource(
				System.getProperty("user.dir") + "\\src\\main\\resources\\pdf\\" + fileName);

		if (resource.exists()) {
			// Set the content disposition header to force download
			String contentDisposition = "attachment; filename=\"" + fileName + "\"";

			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
					.contentType(MediaType.APPLICATION_PDF).body(resource);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	
	@CrossOrigin(origins = "http://localhost:8081")
	@GetMapping(value = "/download/{filename}", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<InputStreamResource> getPdf(@PathVariable String filename) throws IOException {
		File pdfFile = new File(System.getProperty("user.dir")+"/src/main/resources/pdf/" + filename);
		InputStreamResource resource = new InputStreamResource(new FileInputStream(pdfFile));
		if(resource.exists()) {
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename)
					.contentType(MediaType.APPLICATION_PDF).body(resource);
		}else {
			return ResponseEntity.notFound().build();
		}
		
	}

}
