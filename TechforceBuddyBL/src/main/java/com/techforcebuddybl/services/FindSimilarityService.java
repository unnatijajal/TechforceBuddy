package com.techforcebuddybl.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.techforcebuddybl.exception.DataNotFoundException;

public interface FindSimilarityService {
	
	
	public double cosineSimilarity(double[] vecA, double[] vecB);
	
	public Map<String,List<String>> getSimilarityFiles(List<String> extractWord) throws IOException, DataNotFoundException;
	
}
