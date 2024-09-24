package com.techforcebuddybl.services;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface FindSimilarityService {
	
	
	public double cosineSimilarity(double[] vecA, double[] vecB);
	
	public void getSimilarityFiles(List<String> extractWord) throws IOException;
	
	public void extractContent(File file, List<String> keywords) throws IOException;
}
