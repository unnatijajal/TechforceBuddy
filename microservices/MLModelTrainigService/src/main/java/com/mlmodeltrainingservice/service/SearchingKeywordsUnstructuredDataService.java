package com.mlmodeltrainingservice.service;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;



public interface SearchingKeywordsUnstructuredDataService {
	public LinkedHashMap<String, String> getResponsUsingUnstructuredData(String query) throws  Exception;
	
	public LinkedHashMap<String, String> getRelaventFilesResponse(List<String> queryKeywords) throws IOException;
	
	
}
