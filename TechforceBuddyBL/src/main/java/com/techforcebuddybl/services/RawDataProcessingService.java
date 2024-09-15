package com.techforcebuddybl.services;

import java.util.List;

public interface RawDataProcessingService {
	
	public List<String> divideSentenceIntoWords(String data) throws Exception;
	
}
