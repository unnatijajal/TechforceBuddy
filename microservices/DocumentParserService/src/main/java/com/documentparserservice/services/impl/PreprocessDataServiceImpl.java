package com.documentparserservice.services.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.documentparserservice.constant.ConstantDataMember;
import com.documentparserservice.services.PreprocessDataService;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import opennlp.tools.tokenize.WhitespaceTokenizer;

@Service
public class PreprocessDataServiceImpl implements PreprocessDataService {

	
	@Override
	public String[] removeWordStop(String[] lines) throws IOException {
		Set<String> stopWords = new HashSet<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(ConstantDataMember.WORD_STOP_FILE))) {
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
	public String[] tokenizeData(String text) throws IOException {
		WhitespaceTokenizer tokenizer = WhitespaceTokenizer.INSTANCE;
		String[] tokens = tokenizer.tokenize(text);
		return tokens;
	}

	@Override
	public String lemmatizationOfData(String line) throws FileNotFoundException, IOException {
		// Create the properties object to set the properties further
		Properties props = new Properties();

		// Load the properties file for Standford-corenlp.
		props.load(new FileInputStream(ConstantDataMember.STANDFORD_PROPERTIES_FILE));
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		CoreDocument document = pipeline.processToCoreDocument(line);
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
		line = lemmatizedLine.toString().trim();

		return line;
	}

	/*
	 * This method perform the lemmatization on the data. Lemmatization is process
	 * to convert the word into the it's root form Eg : Running will be convert into
	 * Run, Derived will be convert into Derive.
	 * 
	 */
	@Override
	public String[] lemmatizationOfData(String[] lines) throws FileNotFoundException, IOException {

		// Create the properties object to set the properties further
		Properties props = new Properties();

		// Load the properties file for Standford-corenlp.
		props.load(new FileInputStream(ConstantDataMember.STANDFORD_PROPERTIES_FILE));
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
	
	public String[] convertContentIntoLine(String content) {
		return null;
	}

}
