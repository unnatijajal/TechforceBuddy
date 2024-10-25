package com.preprocessuserqueryservice.service;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface ProcessQueryService {
	public String[] tokenizeData(String text) throws IOException;
	public String[] removeWordStop(String[] lines) throws IOException;
	public String[] lemmatizationOfData(String[] lines) throws FileNotFoundException, IOException;
}
