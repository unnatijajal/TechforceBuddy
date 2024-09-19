package com.techforcebuddybl.services.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.techforcebuddybl.services.DataParsingService;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
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
		File wordStopFile = new File(currentDir + "/src/main/resources", "englishStopWords.txt");

		try (BufferedReader reader = new BufferedReader(new FileReader(wordStopFile))) {
			String word;
			while ((word = reader.readLine()) != null) {
				// System.out.println(word);
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

	@Override
	public String[] lemmatizationOfData(String[] lines) throws FileNotFoundException, IOException {

		File stanfordPropertiesFile = new File(currentDir + "/src/main/resources", "standford-corenlp.properties");
		Properties props = new Properties();
		props.load(new FileInputStream(stanfordPropertiesFile));
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

	public void createTrainDataFile() {
		String directoryPath = currentDir + "/src/main/resources/TextFiles";

		// Specify the output file path
		String outputFile = currentDir + "/src/main/resources/TextFiles/TrainData.txt";
		try {
			// Get a list of all text files in the directory
			Files.list(Paths.get(directoryPath)).filter(path -> path.toString().endsWith(".txt")) // filter only text
																									// files
					.forEach(path -> {
						try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
							try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true))) {
								String line;
								while ((line = reader.readLine()) != null) {
									writer.write(line);
									writer.newLine(); // add a newline character
								}
							}
						} catch (IOException e) {
							System.err.println("Error reading file: " + path);
						}
					});
		} catch (IOException e) {
			System.err.println("Error accessing directory: " + directoryPath);
		}
	}
}
