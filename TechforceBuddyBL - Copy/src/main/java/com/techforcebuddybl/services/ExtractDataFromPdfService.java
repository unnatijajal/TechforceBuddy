package com.techforcebuddybl.services;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;

public interface ExtractDataFromPdfService {

	public String extractDataFromPdf(File file);
	
	public void createTextFile(String[] lines, String fileName) throws IOException;
	
	public StringBuilder getContentAfterRemoveFooter(PDDocument document,String fileName) throws IOException;
	
}
