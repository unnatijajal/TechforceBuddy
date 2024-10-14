package com.techforcebuddybl.services.impl;

import java.io.File;
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
	public LinkedHashMap<String,List<String>> getResponsUsingStructuredData(String query) throws DataNotFoundException, Exception {
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
	    	    .collect(Collectors.toMap(
	    	        Map.Entry::getKey, // Keep the original key
	    	        entry -> entry.getValue().stream().limit(2).collect(Collectors.toList()), // Limit the list to 2 elements
	    	        (existing, replacement) -> existing, // Merge function in case of duplicate keys
	    	        LinkedHashMap::new // Use LinkedHashMap as the map type
	    	    ));

	    return finalResult;


	}

}
