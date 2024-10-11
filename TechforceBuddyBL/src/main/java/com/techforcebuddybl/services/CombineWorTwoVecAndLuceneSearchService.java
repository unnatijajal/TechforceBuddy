package com.techforcebuddybl.services;

import java.util.List;
import java.util.Map;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.api.ndarray.INDArray;

public interface CombineWorTwoVecAndLuceneSearchService {

	public INDArray getQueryVector(List<String> keywords, Word2Vec word2Vec);
	public Map<String, Map<String, Double>> refineWithWord2Vec(Word2Vec word2Vec, Map<String, List<String>> luceneResults, List<String> queryKeywords);
	public double computeSimilarity(INDArray queryVector, INDArray sectionVector);
}
