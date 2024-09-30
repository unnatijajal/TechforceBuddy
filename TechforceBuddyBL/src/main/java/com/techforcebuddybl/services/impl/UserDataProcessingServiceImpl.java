package com.techforcebuddybl.services.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public Map<String, List<String>> getResponsAfterProcessQuery(String query) throws DataNotFoundException, Exception {
		// Split the sentence into the words.
		tokens = divideSentenceIntoWords(query.toLowerCase());

		// Invoke the removeWordStop() to remove the stop word from the user's query.
		tokens = dataParsingServiceImpl.removeWordStop(tokens);
		
		// Lemitisation.
		tokens = dataParsingServiceImpl.lemmatizationOfData(tokens);
		
		// Token of user's query will store into the list
		List<String> extractedWord = Arrays.asList(tokens);

		List<String> response = similarityServiceImpl.getRelaventFilesResponse(extractedWord);
		Map<String, List<String>> responseData = new HashMap<>();
		responseData.put("MyFile", response);
		return responseData;
	}

}
