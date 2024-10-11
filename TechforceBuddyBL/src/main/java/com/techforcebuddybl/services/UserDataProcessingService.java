package com.techforcebuddybl.services;

import java.util.List;
import java.util.Map;

import com.techforcebuddybl.exception.DataNotFoundException;

public interface UserDataProcessingService {

	public String[] divideSentenceIntoWords(String data) throws Exception;
	
	Map<String, List<String>> getResponsUsingUnstructuredData(String query) throws DataNotFoundException, Exception;
	
	Map<String, List<String>> getResponsUsingStructuredData(String query) throws DataNotFoundException, Exception;
	
}
