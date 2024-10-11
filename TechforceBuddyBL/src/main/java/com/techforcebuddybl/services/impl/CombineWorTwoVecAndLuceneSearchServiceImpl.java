package com.techforcebuddybl.services.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.stereotype.Service;

import com.techforcebuddybl.services.CombineWorTwoVecAndLuceneSearchService;

@Service
public class CombineWorTwoVecAndLuceneSearchServiceImpl implements CombineWorTwoVecAndLuceneSearchService {

	@Override
	public INDArray getQueryVector(List<String> keywords, Word2Vec word2Vec) {
		INDArray queryVector = Nd4j.zeros(word2Vec.lookupTable().layerSize());

		int validWordCount = 0;
		for (String keyword : keywords) {
			if (word2Vec.hasWord(keyword)) {
				queryVector.addi(word2Vec.getWordVectorMatrix(keyword));
				validWordCount++;
			}
		}

		if (validWordCount > 0) {
			queryVector.divi(validWordCount);
		}

		return queryVector;
	}

	@Override
	public double computeSimilarity(INDArray queryVector, INDArray sectionVector) {
		 // Compute the dot product
	    double dotProduct = Nd4j.getBlasWrapper().dot(queryVector, sectionVector); // Use getDouble(0) to get the value from INDArray

	    // Compute the magnitudes
	    double magnitudeA = Math.sqrt(Nd4j.getBlasWrapper().dot(queryVector, sectionVector)); // Ensure you access INDArray correctly
	    double magnitudeB = Math.sqrt(Nd4j.getBlasWrapper().dot(queryVector, sectionVector));

	    if (magnitudeA == 0 || magnitudeB == 0) {
	        return 0; // Avoid division by zero
	    }

	    return dotProduct / (magnitudeA * magnitudeB);
	}


	public Map<String, Map<String, Double>> refineWithWord2Vec(Word2Vec word2Vec, Map<String, List<String>> luceneResults, List<String> queryKeywords) {
	    INDArray queryVector = getQueryVector(queryKeywords, word2Vec);
	    queryVector = normalizeVector(queryVector);

	    Map<String, Map<String, Double>> refinedResults = new HashMap<>();

	    for (Map.Entry<String, List<String>> entry : luceneResults.entrySet()) {
	        String fileName = entry.getKey();
	        List<String> sections = entry.getValue();

	        Map<String, SectionData> sectionDataMap = new HashMap<>();

	        for (String section : sections) {
	            String[] words = section.split("\\s+");

	            int keywordCount = 0;
	            boolean containsFirstKeyword = false;

	            for (String queryKeyword : queryKeywords) {
	                if (section.toLowerCase().contains(queryKeyword.toLowerCase())) {
	                    keywordCount++;
	                    if (queryKeyword.equalsIgnoreCase(queryKeywords.get(0))) {
	                        containsFirstKeyword = true;
	                    }
	                }
	            }

	            if (keywordCount == 0) {
	                continue;
	            }

	            INDArray sectionVector = Nd4j.zeros(word2Vec.lookupTable().layerSize());
	            int validWordCount = 0;
	            for (String word : words) {
	                if (word2Vec.hasWord(word)) {
	                    sectionVector.addi(word2Vec.getWordVectorMatrix(word));
	                    validWordCount++;
	                }
	            }
	            if (validWordCount > 0) {
	                sectionVector.divi(validWordCount);
	            }
	            sectionVector = normalizeVector(sectionVector);
	            double similarity = computeSimilarity(queryVector, sectionVector);

	            sectionDataMap.put(section, new SectionData(similarity, keywordCount, containsFirstKeyword));
	        }

	        Map<String, Double> finalResults = new LinkedHashMap<>();
	        sectionDataMap.entrySet().stream()
	            .sorted((entry1, entry2) -> {
	                SectionData data1 = entry1.getValue();
	                SectionData data2 = entry2.getValue();

	                int keywordCountComparison = Integer.compare(data2.getKeywordCount(), data1.getKeywordCount());
	                if (keywordCountComparison != 0) {
	                    return keywordCountComparison;
	                }

	                int firstKeywordComparison = Boolean.compare(data2.containsFirstKeyword(), data1.containsFirstKeyword());
	                if (firstKeywordComparison != 0) {
	                    return firstKeywordComparison;
	                }

	                return Double.compare(data2.getSimilarity(), data1.getSimilarity());
	            })
	            .forEach(resultEntry -> {
	                if (resultEntry.getValue().getSimilarity() > 0) {
	                    finalResults.put(resultEntry.getKey(), resultEntry.getValue().getSimilarity());
	                }
	            });

	        refinedResults.put(fileName, finalResults); // Store results by file
	    }

	    return refinedResults;
	}


	
	private INDArray normalizeVector(INDArray vector) {
	    // Calculate the L2 norm manually
	    double norm = Math.sqrt(vector.mul(vector).sumNumber().doubleValue());

	    // Check if norm is zero to avoid division by zero
	    if (norm == 0) {
	        return vector; // or return Nd4j.zeros(vector.shape()); for a zero vector
	    }

	    // Normalize the vector by dividing each component by the norm
	    return vector.div(norm);
	}
	
	class SectionData {
	    private double similarity;
	    private int keywordCount;
	    private boolean containsFirstKeyword;

	    // Constructor
	    public SectionData(double similarity, int keywordCount, boolean containsFirstKeyword) {
	        this.similarity = similarity;
	        this.keywordCount = keywordCount;
	        this.containsFirstKeyword = containsFirstKeyword;
	    }

	    // Getters
	    public double getSimilarity() {
	        return similarity;
	    }

	    public int getKeywordCount() {
	        return keywordCount;
	    }

	    public boolean containsFirstKeyword() {
	        return containsFirstKeyword;
	    }
	}

}
