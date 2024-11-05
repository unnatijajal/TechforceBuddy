package com.mlmodeltrainingservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import com.fasterxml.jackson.databind.JsonNode;

@FeignClient("DOCUMENTPARSERSERVICE")
public interface GetRawData {
	@CrossOrigin(origins = "*")
	@GetMapping("/getTextFileContent")
	public ResponseEntity<?> getContentOfTextFiles();
	
	@CrossOrigin(origins = "*")
	@GetMapping("/getJsonFileContent")
	public ResponseEntity<JsonNode> getContentOfJsonFile();
}
