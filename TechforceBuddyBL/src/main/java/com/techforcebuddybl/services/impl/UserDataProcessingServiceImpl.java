package com.techforcebuddybl.services.impl;

import java.io.File;
import java.util.Arrays;
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
	String[] tags;

	@Override
	public String[] divideSentenceIntoWords(String data) throws Exception {
		try {
			// Invoke the tokenizeData() to convert the query into the tokens.
			tokens = parsingServiceImpl.tokenizeData(data);

			// Invoke the paringData() which give the tag of tokens
			// tags = parsingServiceImpl.parsingData(tokens);

			// Invoke the removeWordStop() to remove the stop word from the user's query.
			tokens = dataParsingServiceImpl.removeWordStop(tokens);

			return tokens;

		} catch (Exception e) {
			throw e;
		}
	}

	public Map<String, List<String>> getResponsAfterProcessQuery(String query) throws DataNotFoundException, Exception {
		String[] tokens = divideSentenceIntoWords(query);
		// Token of user's query will store into the list
		List<String> extractedWord = Arrays.asList(tokens);

		// Invoke the getSimilarityFiles() to find the similarity file
		// means this file content have similarity with user's query.
		List<File> relaventFile = similarityServiceImpl.getRelaventFiles(extractedWord);
		
		Map<String, List<String>> responseData = similarityServiceImpl.getTheResponseFromRelaventFile(relaventFile, extractedWord);
		return responseData;
	}

}
