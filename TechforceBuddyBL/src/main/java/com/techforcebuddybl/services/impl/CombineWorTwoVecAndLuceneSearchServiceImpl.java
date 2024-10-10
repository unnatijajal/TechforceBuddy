package com.techforcebuddybl.services.impl;

import java.util.HashMap;
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

	@Override
	public Map<String, Double> refineWithWord2Vec(Word2Vec word2Vec, List<String> luceneResults, List<String> queryKeywords) {
		INDArray queryVector = getQueryVector(queryKeywords, word2Vec);
		// After the normalize the queryvector
		queryVector = normalizeVector(queryVector);

		Map<String, Double> similarityScores = new HashMap<>();
		for(String section : luceneResults) {
			String[] words = section.split("\\s+");
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
            similarityScores.put(section, similarity);
		}
		 Map<String, Double> finalResults = new HashMap<>();
		similarityScores.entrySet().stream()
			.sorted(Map.Entry.<String, Double>comparingByValue().reversed())
			.forEach(entry -> {
				if(entry.getValue() > 0)
					finalResults.put(entry.getKey(), entry.getValue());
			});
		
		for(Map.Entry<String, Double> entry : finalResults.entrySet()) {
			System.out.println(entry.getKey()+"\n"+entry.getValue());
		}
		return finalResults;
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


}
