package com.mlmodeltrainingservice.feign;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("PYTHON-SERVICE")
public interface GetSummaryOfResponse {

	@PostMapping("/generate")
	@CrossOrigin(origins = "http://localhost:8080")
	Map<String, Object> generateSummary(@RequestBody
			Map<String, String> requestBody);
	
}
