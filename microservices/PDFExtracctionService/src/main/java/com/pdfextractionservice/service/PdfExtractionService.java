package com.pdfextractionservice.service;

import java.io.IOException;
import java.util.LinkedHashMap;

public interface PdfExtractionService {
	public LinkedHashMap<String,String> getContentFromPdf() throws IOException;
}
