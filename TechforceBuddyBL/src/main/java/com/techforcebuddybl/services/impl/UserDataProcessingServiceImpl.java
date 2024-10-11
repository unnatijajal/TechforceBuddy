package com.techforcebuddybl.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
	public Map<String,String> getResponsUsingUnstructuredData(String query) throws DataNotFoundException, Exception {
		// Split the sentence into the words.
		tokens = divideSentenceIntoWords(query.toLowerCase());

		// Invoke the removeWordStop() to remove the stop word from the user's query.
		tokens = dataParsingServiceImpl.removeWordStop(tokens);

		// Lemitisation.
		tokens = dataParsingServiceImpl.lemmatizationOfData(tokens);

		// Token of user's query will store into the list
		List<String> extractedWord = Arrays.asList(tokens);
		Map<String,String> response = similarityServiceImpl.getRelaventFilesResponse(extractedWord);
		

		return response;
	}

	// This is the method to get the data using structured data

	@Override
	public List<String> getResponsUsingStructuredData(String query) throws DataNotFoundException, Exception {
	    tokens = divideSentenceIntoWords(query.toLowerCase());
	    tokens = dataParsingServiceImpl.removeWordStop(tokens);
	    tokens = dataParsingServiceImpl.lemmatizationOfData(tokens);

	    List<String> extractedWord = Arrays.asList(tokens);

	    @SuppressWarnings("deprecation")
	    Word2Vec word2Vec = WordVectorSerializer.readWord2Vec(
	            new File(System.getProperty("user.dir") + "/src/main/resources/AiModal/word2vecModel.bin"));
	     
	    Map<String, List<String>> relevantSection;
	    
	    relevantSection = tfidfSimilarity.searchRelevantSections(extractedWord);
	    
	    Map<String, Map<String, Double>> answer;
	    
	    answer = searchServiceImpl.refineWithWord2Vec(word2Vec, relevantSection, extractedWord);
	    List<String> rewrittenSections = new ArrayList<>();
	    answer.forEach((fileName, sectionMap) -> {
	        sectionMap.entrySet().stream().limit(2).forEach(entry -> {
	            rewrittenSections.add(entry.getKey()+"\n[Reference : "+fileName+"]");
	        });
	    });

	    return rewrittenSections;
	}

}
