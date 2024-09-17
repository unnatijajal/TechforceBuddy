package com.techforcebuddybl.services;

import java.io.IOException;

public interface DataParsingService {

	public String[] tokenizeData(String text) throws IOException;
	
	public String[] parsingData(String[] tokens) throws IOException;
 	
	public boolean isNounOrVerb(String posTag);
}
