package com.techforcebuddybl.services;

import java.util.Map;

import com.techforcebuddybl.exception.DataNotFoundException;

public interface UserDataProcessingService {

	public String[] divideSentenceIntoWords(String data) throws Exception;
	
	Map<String,String> getResponsUsingUnstructuredData(String query) throws DataNotFoundException, Exception;
	
	Map<String,String> getResponsUsingStructuredData(String query) throws DataNotFoundException, Exception;
	
}
