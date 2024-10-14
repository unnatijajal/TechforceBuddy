package com.techforcebuddybl.services;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.techforcebuddybl.exception.DataNotFoundException;

public interface UserDataProcessingService {

	public String[] divideSentenceIntoWords(String data) throws Exception;
	
	Map<String,String> getResponsUsingUnstructuredData(String query) throws DataNotFoundException, Exception;
	
	LinkedHashMap<String,List<String>> getResponsUsingStructuredData(String query) throws DataNotFoundException, Exception;
	
}
