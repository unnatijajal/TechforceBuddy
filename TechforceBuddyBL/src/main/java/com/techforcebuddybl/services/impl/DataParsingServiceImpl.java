package com.techforcebuddybl.services.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.techforcebuddybl.services.DataParsingService;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;

@Service
public class DataParsingServiceImpl implements DataParsingService {

	// Get the current directory
	private String currentDir = System.getProperty("user.dir");

	@Override
	public String[] tokenizeData(String text) throws IOException {
		WhitespaceTokenizer tokenizer = WhitespaceTokenizer.INSTANCE;
		String[] tokens = tokenizer.tokenize(text);
		return tokens;
	}

	@Override
	public String[] parsingData(String[] tokens) throws IOException {
		String currentDir = System.getProperty("user.dir");
		File resourceDir = new File(currentDir + "/src/main/resources/OpenNlp_models");
		FileInputStream inputStream = new FileInputStream(new File(resourceDir, "en-pos-maxent.bin"));
	
		POSModel model = new POSModel(inputStream);
		POSTaggerME tagger = new POSTaggerME(model);
		return tagger.tag(tokens);
	}

	@Override
	public boolean isNounOrVerb(String posTag) {
		return posTag.startsWith("NN") || posTag.startsWith("VB") || posTag.startsWith("JJ");
	}

	public String[] removeWordStop(String[] lines) throws IOException {
		Set<String> stopWords = new HashSet<>();
		// Navigate to the TextFiles directory
		File wordStopFile = new File(currentDir + "/src/main/resources","englishStopWords.txt");
		
		try (BufferedReader reader = new BufferedReader(new FileReader(wordStopFile))) {
			String word;
			while ((word = reader.readLine()) != null) {
				//System.out.println(word);
				stopWords.add(word.toLowerCase());
			}
		}
		// iterate over the lines and remove stop words
		for (int i = 0; i < lines.length; i++) {
		    String line = lines[i];
		    String[] words = line.split("\\s+"); // split the line into individual words
		    StringBuilder newLine = new StringBuilder();
		    for (String word : words) {
		        if (!stopWords.contains(word.toLowerCase())) { // check if the word is not a stop word
		            newLine.append(word).append(" ");
		        }
		    }
		    lines[i] = newLine.toString().trim(); // update the line with the new string
		}
		
		return lines;
	}

}
