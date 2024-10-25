package com.documentparserservice.services.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.documentparserservice.constant.ConstantDataMember;
import com.documentparserservice.services.FetchFileContentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FetchFileContentServiceImpl implements FetchFileContentService{

	
	@Override
	public List<String> getTextFileContent() throws IOException {
		File inputDir = new File(ConstantDataMember.TEXT_FILE_DIR);
		List<String> sentences = new ArrayList<>();
 		 // Loop through all files in the directory and read content
	    if (inputDir.isDirectory()) {
	        for (File file : inputDir.listFiles()) {
	            if (file.isFile() && file.getName().endsWith(".txt")) {
	                // Read all lines from the file and add them to the list
	                List<String> lines = Files.readAllLines(Paths.get(file.getAbsolutePath()));
	                sentences.addAll(lines);
	            }
	        }
	    }
		return sentences;
	}

	@Override
	public JsonNode getJSonFileContent() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(
				new File(ConstantDataMember.JSON_DIR+"/output.json"));
		return rootNode;
	}

}
