package com.techforcebuddybl.services.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techforcebuddybl.services.ExtractDataFromPdfService;
import com.techforcebuddybl.util.ConstantData;

@Service
public class ExtractDataFromPdfServiceImpl implements ExtractDataFromPdfService {

	public static Map<String, List<String>> topWordsByFile = new HashMap<String, List<String>>();

	@Autowired
	private ConstantData constantData;

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
			/*
			 * for (int i = 0; i < lines.length; i++) {
			 * constantData.createKeywords(file.getName(), lines[i]); }
			 */
			try {
				lines = dataParsingServiceImpl.removeWordStop(lines);
				lines = dataParsingServiceImpl.lemmatizationOfData(lines);
				createTextFile(lines, file.getName());
			} catch (IOException e) {
				System.out.println("Exception " + e.getMessage());
			}
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
				/*
				 * tfidfUtils.indexDocument(String.join(" ",
				 * tfidfUtils.preprocessText(lines[i]))); try { List<String> topWords =
				 * tfidfUtils.calculateTFIDF(100); topWordsByFile.put(fileName, topWords); }
				 * catch (Exception e) { // TODO Auto-generated catch block e.printStackTrace();
				 * }
				 */
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}