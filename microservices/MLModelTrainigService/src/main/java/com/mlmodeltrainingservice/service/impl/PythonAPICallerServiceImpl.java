package com.mlmodeltrainingservice.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mlmodeltrainingservice.service.PythonAPICallerService;

@Service
public class PythonAPICallerServiceImpl implements PythonAPICallerService {

	@Override
	public String callGenerateSummaryApi(String inputText) throws Exception {
		String url = "http://localhost:5000/generate"; // Python API URL
		RestTemplate restTemplate = new RestTemplate();

		// Prepare the payload as JSON with input_text key
		Map<String, String> payload = new HashMap<>();
		payload.put("input_text", inputText.toString());

		// Prepare headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// Send request to Python API
		HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(payload, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(response.getBody());
		String summaryText = rootNode.path("generated_text").get(0).path("summary_text").asText();
		return summaryText; // Return only the summary_text value

	}

}
