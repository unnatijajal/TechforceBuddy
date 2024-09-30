package com.techforcebuddybl.services.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techforcebuddybl.exception.DataNotFoundException;
import com.techforcebuddybl.services.ExtractDataFromPdfService;

/*
 * This is class which have methods for extracting data from the pdf files.
 * This is implementation class of ExtractDataFormPdfService.
 */

@Service
public class ExtractDataFromPdfServiceImpl implements ExtractDataFromPdfService {

	// Get the current directory
	private String currentDir = System.getProperty("user.dir");

	@Autowired
	private DataParsingServiceImpl dataParsingServiceImpl;

	/*
	 * This the method for process the data from the pdf files
	 */
	public void processDataOfPDF() throws IOException {

		// Navigate to the src/main/resources directory
		File resourceDir = new File(currentDir + "/src/main/resources/pdf");
		// Get the list of files
		String[] files = resourceDir.list();
		// Iterate over the files
		for (String fileName : files) {

			// Check if the file is a PDF file
			if (fileName.endsWith(".pdf")) {
				// Get the file
				File file = new File(resourceDir, fileName);
				// Invoke the method to extract the data from the pdf
				String text = extractDataFromPdf(file);
				/*
				 * Used regex to remove all bulleting and punctuation extra white spaces from
				 * the text for data Pre-Process
				 */
				text = text.replaceAll("[,•&&[^\\n]]+|[-—]+|[\\p{Punct}&&[^\u002E]]", "");

				// Split the text into the array of string
				String[] lines = text.split("\n");

				try {

					// Invoke the removeWordStop() to remove all the stop words.
					lines = dataParsingServiceImpl.removeWordStop(lines);

					// Invoke the lemmatizationOfData() to convert the word into it's root form.
					lines = dataParsingServiceImpl.lemmatizationOfData(lines);

					// Invoke the method to create the Text file of Pre-Process the data
					createTextFile(lines, file.getName());

				} catch (DataNotFoundException exception) {
					System.out.println(exception.getMessage());
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}

			}
		}
	}

	/*
	 * This method is extract the data from the pdf file using Apache PdfBox
	 */
	@Override
	public String extractDataFromPdf(File file) {
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
			text = getContentAfterRemoveFooter(document, text).toString();
		} catch (IOException exception) {
			System.out.println(exception);
		}
		if (!text.equalsIgnoreCase("")) {
			return text;
		} else {
			throw new DataNotFoundException("Something went wrong while extracting the data from the pdf!!");
		}
	}

	/*
	 * This is the method of creating text file of pre-proccessed data
	 */
	@Override
	public void createTextFile(String[] lines, String fileName) throws IOException {
		// Navigate to the TextFiles directory
		File textFileDir = new File(currentDir + "/src/main/resources/TextFiles");

		// Create the file object for text file
		File textFile = new File(textFileDir, fileName.substring(0, fileName.lastIndexOf(".")) + ".txt");

		// Create the object of the FileWriter to write the data into the TextFile.
		try (FileWriter fileWriter = new FileWriter(textFile)) {
			for (int i = 0; i < lines.length; i++) {
				fileWriter.write(lines[i] + "\n");
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

	}

	@Override
	public StringBuilder getContentAfterRemoveFooter(PDDocument document, String fileName) throws IOException {
		// PDFTextStripper to extract the text from each page
		PDFTextStripper textStripper = new PDFTextStripper();

		// Iterate through the pages
		int numberOfPages = document.getNumberOfPages();
		StringBuilder modifiedContent = new StringBuilder();

		for (int i = 2; i < numberOfPages; i++) {
			textStripper.setStartPage(i);
			textStripper.setEndPage(i);

			// Extract the text of the page
			String pageText = textStripper.getText(document);

			// Split the text into lines
			String[] lines = pageText.split("\\n");

			// Check if the page has more than two lines
			if (lines.length > 2) {

				for (int j = 0; j < lines.length - 2; j++) {
					modifiedContent.append(lines[j]).append(System.lineSeparator());
				}
			}

		}
		return modifiedContent;
	}

}