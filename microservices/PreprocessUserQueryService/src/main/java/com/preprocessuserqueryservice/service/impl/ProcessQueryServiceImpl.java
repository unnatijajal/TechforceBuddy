package com.preprocessuserqueryservice.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.preprocessuserqueryservice.service.ProcessQueryService;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import opennlp.tools.tokenize.WhitespaceTokenizer;

@Service
public class ProcessQueryServiceImpl implements ProcessQueryService {

	private static final File STANFORD_PROPERTIES_FILE = new File(
			System.getProperty("user.dir") + "/src/main/resources", "standford-corenlp.properties");

	// Navigate to the TextFiles directory
	private static final File WORD_STOP_FILE = new File(System.getProperty("user.dir") + "/src/main/resources",
			"englishStopWords.txt");

	// This method convert the text into the token using whitespace to convert into
	// the token.
	@Override
	public String[] tokenizeData(String text) throws IOException {
		WhitespaceTokenizer tokenizer = WhitespaceTokenizer.INSTANCE;
		String[] tokens = tokenizer.tokenize(text);
		return tokens;
	}

	/*
	 * This is the method which remove the all the stop words like and, he, she etc
	 * from the lines of files.
	 * 
	 */
	@Override
	public String[] removeWordStop(String[] lines) throws IOException {
		Set<String> stopWords = new HashSet<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(WORD_STOP_FILE))) {
			String word;
			while ((word = reader.readLine()) != null) {
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

		return Arrays.stream(lines).filter(line -> !line.isEmpty()).toArray(String[]::new);
	}

	@Override
	public String[] lemmatizationOfData(String[] lines) throws FileNotFoundException, IOException {
		// Create the properties object to set the properties further
		Properties props = new Properties();

		// Load the properties file for Standford-corenlp.
		props.load(new FileInputStream(STANFORD_PROPERTIES_FILE));
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		// Lemmatize each line of text
		for (int i = 0; i < lines.length; i++) {

			CoreDocument document = pipeline.processToCoreDocument(lines[i]);
			// Iterate over the sentences and tokens to extract the lemmas
			StringBuilder lemmatizedLine = new StringBuilder();
			for (CoreLabel token : document.tokens()) {
				String lemma = token.lemma();
				// Prioritize lemmatization for verb participles (VBG)
				if (token.tag().equals("VBG")) {
					lemma = token.lemma();
				}
				lemmatizedLine.append(lemma).append(" ");
			}
			lines[i] = lemmatizedLine.toString().trim();
		}

		return lines;
	}
	
	public List<String> getQueryKeywords(String query) throws IOException {
		
		String[] tokens = tokenizeData(query);  // Divide the query into the words/tokens
		tokens = removeWordStop(tokens); // remove the stop words from the tokens
		tokens = lemmatizationOfData(tokens); // perform lemmatization on tokens. 
		
		return Arrays.asList(tokens);
	}

}
