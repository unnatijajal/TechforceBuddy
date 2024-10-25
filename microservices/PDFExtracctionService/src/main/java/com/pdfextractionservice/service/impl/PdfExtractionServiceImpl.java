package com.pdfextractionservice.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import com.pdfextractionservice.exception.DataNotFoundException;
import com.pdfextractionservice.service.PdfExtractionService;

@Service
public class PdfExtractionServiceImpl implements PdfExtractionService {

	private static final String pdfDir = System.getProperty("user.dir") + "/src/main/resources/pdf";

	@Override
	public LinkedHashMap<String,String> getContentFromPdf() throws IOException {
		String text="";
		File resourceDir = new File(pdfDir);
		LinkedHashMap<String, String> contentWithFileName = new LinkedHashMap<String, String>();
		// Get the list of files
		String[] files = resourceDir.list();
		// Iterate over the files
		for (String fileName : files) {

			// Check if the file is a PDF file
			if (fileName.endsWith(".pdf")) {
				// Get the file
				File file = new File(resourceDir, fileName);
				// Invoke the method to extract the data from the pdf
				text = extractDataFromPdf(file);
				if(text!=null)
					contentWithFileName.put(fileName, text);
				else
					throw new NullPointerException("Data not found in pdf for file : "+fileName);
			}
		}
		return contentWithFileName;
		
	}
	
	public String extractDataFromPdf(File file) throws IOException {
		String text = "";
		try {

			// Create the document to load the file.
			PDDocument document = PDDocument.load(file);

			// Create the object of PDFTextStripper which is help to extract the data from
			// the pdf.
			PDFTextStripper textStripper = new PDFTextStripper();

			// Set the staring page the extract the data.
			textStripper.setStartPage(3);

			// Get the text from the pdf file.
			text = textStripper.getText(document);
			if (!text.equalsIgnoreCase("")) {
				document.close();
				return text;
			} else {
				throw new DataNotFoundException("Something went wrong while extracting the data from the pdf!!");
			}
		} catch (IOException exception) {
			throw exception;
		}
		
	}

}
