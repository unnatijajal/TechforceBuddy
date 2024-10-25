package com.documentparserservice.services.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.documentparserservice.constant.ConstantDataMember;
import com.documentparserservice.services.GenerateFileService;

@Service
public class GenerateFileServiceImpl implements GenerateFileService {

	@Autowired
	private CreateJsonOfPdfServiceImpl createJsonOfPdfServiceImpl;
	
	@Override
	public void createTextFile(String content, String fileName) throws IOException {
		//Remove all punctuation from the content of the pdf
		content = content.replaceAll("[\\p{Punct}&&[^\u002E]]", "");

		// Split the text into the array of string
		String[] lines = content.split("\n");
		// Navigate to the TextFiles directory
		File textFileDir = new File(ConstantDataMember.TEXT_FILE_DIR);

		// Create the file object for text file
		File textFile = new File(textFileDir, fileName.substring(0, fileName.lastIndexOf(".")) + ".txt");

		// Create the object of the FileWriter to write the data into the TextFile.
		try (FileWriter fileWriter = new FileWriter(textFile)) {
			for (int i = 0; i < lines.length; i++) {
				fileWriter.write(lines[i] + "\n");
			}
		} catch (IOException e) {
			throw e;
		}

	}

	@Override
	public void createJsonFile(LinkedHashMap<String,String> contentWithFileName) throws IOException {
		
		for(Map.Entry<String, String> entry : contentWithFileName.entrySet()) {
			// Split the text into the array of string
			String[] lines = entry.getValue().split("\n");
			createJsonOfPdfServiceImpl.createJsonOfPdf(lines,entry.getKey());
		}
		createJsonOfPdfServiceImpl.saveJSONToFile();
		
		
	}
	
	

}
