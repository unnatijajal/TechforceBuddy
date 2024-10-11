package com.techforcebuddybl.services;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface SentinizeAnalyzerService {

	public String analyzeSentiment(String content) throws FileNotFoundException, IOException;
	
}
