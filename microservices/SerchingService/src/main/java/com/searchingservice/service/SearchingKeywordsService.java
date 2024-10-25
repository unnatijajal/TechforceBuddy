package com.searchingservice.service;

import java.util.List;
import java.util.Map;

public interface SearchingKeywordsService {
	
	public Map<String, String> searchingKeywordsForUnstructuredData(String query);
	
	public void searchingKeywordsForStructuredData(List<String> queryKeywords);
	
	public double cosineSimilarity(double[] vecA, double[] vecB);
	
}
