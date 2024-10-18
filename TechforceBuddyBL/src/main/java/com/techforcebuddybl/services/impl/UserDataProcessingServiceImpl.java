package com.techforcebuddybl.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techforcebuddybl.exception.DataNotFoundException;
import com.techforcebuddybl.services.UserDataProcessingService;

/*
 * This is the implementation class of UserDataProcessingService
 * Which process the user's query.
 */

@Service
public class UserDataProcessingServiceImpl implements UserDataProcessingService {

	@Autowired
	DataParsingServiceImpl parsingServiceImpl;

	@Autowired
	private DataParsingServiceImpl dataParsingServiceImpl;

	@Autowired
	private FindSimilarityServiceImpl similarityServiceImpl;

	@Autowired
	private TFIDFSimilarity tfidfSimilarity;

	@Autowired
	private CombineWorTwoVecAndLuceneSearchServiceImpl searchServiceImpl;

	String[] tokens;

	@Override
	public String[] divideSentenceIntoWords(String data) throws Exception {
		try {
			// Invoke the tokenizeData() to convert the query into the tokens.
			tokens = parsingServiceImpl.tokenizeData(data);
			return tokens;

		} catch (Exception e) {
			throw e;
		}
	}

	// This is the method to get the response using unstructured data
	@Override
	public Map<String, String> getResponsUsingUnstructuredData(String query) throws DataNotFoundException, Exception {
		// Split the sentence into the words.
		tokens = divideSentenceIntoWords(query.toLowerCase());

		// Invoke the removeWordStop() to remove the stop word from the user's query.
		tokens = dataParsingServiceImpl.removeWordStop(tokens);

		// Lemitisation.
		tokens = dataParsingServiceImpl.lemmatizationOfData(tokens);

		// Token of user's query will store into the list
		List<String> extractedWord = Arrays.asList(tokens);
		Map<String, String> response = similarityServiceImpl.getRelaventFilesResponse(extractedWord);

		return response;
	}

	// This is the method to get the data using structured data

	@Override
	public LinkedHashMap<String, List<String>> getResponsUsingStructuredData(String query)
			throws DataNotFoundException, Exception {
		tokens = divideSentenceIntoWords(query.toLowerCase());
		tokens = dataParsingServiceImpl.removeWordStop(tokens);
		tokens = dataParsingServiceImpl.lemmatizationOfData(tokens);

		List<String> extractedWord = Arrays.asList(tokens);

		@SuppressWarnings("deprecation")
		Word2Vec word2Vec = WordVectorSerializer.readWord2Vec(
				new File(System.getProperty("user.dir") + "/src/main/resources/AiModal/word2vecModel.bin"));

		Map<String, List<String>> relevantSection;

		relevantSection = tfidfSimilarity.searchRelevantSections(extractedWord);

		LinkedHashMap<String, List<String>> answer;

		answer = searchServiceImpl.refineWithWord2Vec(word2Vec, relevantSection, extractedWord);

		LinkedHashMap<String, List<String>> finalResult = answer.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, // Keep the original key
						entry -> entry.getValue().stream().limit(2).collect(Collectors.toList()), // Limit the list to 2
																									// elements
						(existing, replacement) -> existing, // Merge function in case of duplicate keys
						LinkedHashMap::new // Use LinkedHashMap as the map type
				));

		LinkedHashMap<String, List<String>> formattedResults = formatRefinedResults(finalResult);
		return formattedResults;
	}

	public static LinkedHashMap<String, List<String>> formatRefinedResults(
			LinkedHashMap<String, List<String>> refinedResults) {
		LinkedHashMap<String, List<String>> formattedResults = new LinkedHashMap<>();

		for (Map.Entry<String, List<String>> entry : refinedResults.entrySet()) {
			String fileName = entry.getKey();
			List<String> sections = entry.getValue();
			List<String> formattedSections = new ArrayList<>();

			for (String section : sections) {
				String[] parts = section.split("\n", 3); // Split into heading, subheading, and section

				String formattedHeading = parts.length > 0 ? boldText(parts[0].toUpperCase()) : "";
				String formattedSubheading = parts.length > 1 ? toTitleCase(parts[1]) : "";
				String formattedSection = parts.length > 2 ? formatSectionWithBullets(parts[2]) : "";

				// Add the formatted content to the list
				formattedSections.add(formattedHeading);
				if (!formattedSubheading.isEmpty()) {
					formattedSections.add(formattedSubheading);
				}
				formattedSections.add(formattedSection);
			
			}

			// Store the formatted sections in the result map
			formattedResults.put(fileName, formattedSections);
		}

		return formattedResults;
	}

	// Make text bold using HTML tags
	public static String boldText(String text) {
		return "<strong>" + text + "</strong>"; // Use HTML <strong> tags for bold text
	}

	// Convert a string to title case
	public static String toTitleCase(String input) {
		if (input == null || input.isEmpty()) {
			return input;
		}
		String[] words = input.split(" ");
		StringBuilder titleCase = new StringBuilder();

		for (String word : words) {
			if (word.length() > 1) {
				titleCase.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase());
			} else {
				titleCase.append(word.toUpperCase());
			}
			titleCase.append(" ");
		}
		return titleCase.toString().trim();
	}

	// Format the section content with bullet points based on complete sentences
	public static String formatSectionWithBullets(String section) {
	    StringBuilder formattedSection = new StringBuilder();
	    StringBuilder currentSentence = new StringBuilder();

	    // Split the section into sentences based on full stops followed by spaces
	    String[] sentences = section.split("(?<=\\.)\\s*"); // Adjust regex to split by full stop

	    for (String sentence : sentences) {
	        // Replace newlines with spaces if they do not precede a period
	        sentence = sentence.replaceAll("\n(?!\\s*\\.)", " ");

	        if (!sentence.trim().isEmpty()) {
	            currentSentence.append(sentence.trim());

	            // Check if the current sentence ends with a full stop
	            if (currentSentence.toString().endsWith(".")) {
	                // Only add a bullet if the formatted section does not already start with a bullet
	                if (!formattedSection.toString().trim().startsWith("•")) {
	                    formattedSection.append("• ").append(currentSentence.toString().trim()).append("\n");
	                } else {
	                    // Append the current sentence without adding another bullet
	                    formattedSection.append(currentSentence.toString().trim()).append("\n");
	                }
	                currentSentence.setLength(0); // Reset currentSentence for the next sentence
	            }
	        }
	    }

	    // If there are any remaining sentences not ending with a full stop
	    if (currentSentence.length() > 0) {
	        // Only add a bullet if the formatted section does not already start with a bullet
	        if (!formattedSection.toString().trim().startsWith("•")) {
	            formattedSection.append("• ").append(currentSentence.toString().trim()).append("\n");
	        } else {
	            // Append the current sentence without adding another bullet
	            formattedSection.append(currentSentence.toString().trim()).append("\n");
	        }
	    }

	    return formattedSection.toString().trim();
	}



}
