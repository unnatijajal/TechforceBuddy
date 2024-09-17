package com.techforcebuddybl.services.impl;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techforcebuddybl.services.ExtractDataFromPdfService;

@Service
public class ExtractDataFromPdfServiceImpl implements ExtractDataFromPdfService {

	// Get the current directory
	private String currentDir = System.getProperty("user.dir");
	
	@Autowired
	private DataParsingServiceImpl dataParsingServiceImpl;

	public void accessFiles() throws IOException {

		// Navigate to the src/main/resources directory
		File resourceDir = new File(currentDir + "/src/main/resources/pdf");

		// Get the list of files
		String[] files = resourceDir.list();

		// Iterate over the files
		for (String fileName : files) {

			// Check if the file is a PDF file
			if (fileName.endsWith(".pdf")) {
				// Get the file
				File f = new File(resourceDir, fileName);
				extractDataFromPdf(f);
				
			}
		}
	}

	@Override
	public void extractDataFromPdf(File file) {
		try {
			PDDocument document = Loader.loadPDF(file);

			PDFTextStripper textStripper = new PDFTextStripper();

			textStripper.setStartPage(3);
			String text = textStripper.getText(document);

			text = text.replaceAll("[,.•&&[^\\n]]+|[-—]+|[\\p{Punct}]", "");
			String[] lines = text.split("\n");
			
			lines = dataParsingServiceImpl.removeWordStop(lines);
			

			createTextFile(lines, file.getName());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void createTextFile(String[] lines, String fileName) throws IOException {
		// Navigate to the TextFiles directory		
		File textFileDir = new File(currentDir + "/src/main/resources/TextFiles");

		File textFile = new File(textFileDir, fileName.substring(0, fileName.lastIndexOf(".")) + ".txt");

		try (FileWriter fileWriter = new FileWriter(textFile)) {
			for (int i = 0; i < lines.length; i++) {

				fileWriter.write(lines[i] + "\n");
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}