package com.mlmodeltrainingservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("PREPROCESSUSERQUERYSERVICE")
public interface ProcessTheQuery {
	@PostMapping("/processQuery")
	//@CrossOrigin(origins = "*")
	public ResponseEntity<?> getQuerKeywords(@RequestParam String query);
}
