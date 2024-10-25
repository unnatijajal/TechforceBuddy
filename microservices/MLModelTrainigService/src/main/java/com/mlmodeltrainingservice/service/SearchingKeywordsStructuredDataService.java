package com.mlmodeltrainingservice.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.api.ndarray.INDArray;

public interface SearchingKeywordsStructuredDataService {

	public INDArray getQueryVector(List<String> keywords, Word2Vec word2Vec);
	public Map<String, List<String>> refineWithWord2Vec(Word2Vec word2Vec, Map<String, List<String>> luceneResults, List<String> queryKeywords);
	public double computeSimilarity(INDArray queryVector, INDArray sectionVector);
	LinkedHashMap<String, List<String>> getResponsUsingStructuredData(String query) throws Exception;
}
