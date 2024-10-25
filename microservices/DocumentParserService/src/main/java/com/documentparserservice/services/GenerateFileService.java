package com.documentparserservice.services;

import java.io.IOException;
import java.util.LinkedHashMap;

public interface GenerateFileService {

	public void createTextFile(String content, String fileName) throws IOException;
	
	public void createJsonFile(LinkedHashMap<String,String> contentWithFileName) throws IOException;
}
