package com.documentparserservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("PDFEXTRACCTIONSERVICE")
public interface PdfExtraction {

	@GetMapping("pdfExtraction/getContent")
//	@CrossOrigin(origins = "*")
	public ResponseEntity<?> getContentOfPdf();
}
