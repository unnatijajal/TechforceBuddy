package com.techforcebuddybl.services;

import java.util.List;
import java.util.Map;

import com.techforcebuddybl.exception.DataNotFoundException;

public interface UserDataProcessingService {

	public Map<String, List<String>> divideSentenceIntoWords(String data) throws DataNotFoundException, Exception;
	
}
