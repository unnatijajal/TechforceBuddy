package com.techforcebuddybl.services;

import java.util.Map;
import java.util.Set;

public interface UserDataProcessingService {

	public Map<String,Set<String>> divideSentenceIntoWords(String data) throws Exception;
	
}
