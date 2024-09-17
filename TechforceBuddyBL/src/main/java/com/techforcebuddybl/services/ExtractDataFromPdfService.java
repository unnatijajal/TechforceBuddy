package com.techforcebuddybl.services;

import java.io.File;
import java.io.IOException;

public interface ExtractDataFromPdfService {

	public void extractDataFromPdf(File file);
	
	public void createTextFile(String[] lines, String fileName) throws IOException;
	
}
