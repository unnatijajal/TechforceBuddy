package com.techforcebuddybl.services;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.deeplearning4j.models.word2vec.Word2Vec;

public interface FindSimilarityService {
	
	public double[] getAveragePolicyVector(Word2Vec model, File policyFile) throws IOException;
	
	public double cosineSimilarity(double[] vecA, double[] vecB);
	
	public void getSimilarityFiles(List<String> extractWord) throws IOException;
	
	public void extractContent(File file, List<String> keywords) throws IOException;
}
