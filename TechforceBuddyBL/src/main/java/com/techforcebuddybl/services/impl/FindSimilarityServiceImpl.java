package com.techforcebuddybl.services.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.FileSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.springframework.stereotype.Service;

import com.techforcebuddybl.services.FindSimilarityService;

@Service
public class FindSimilarityServiceImpl implements FindSimilarityService {

	private String fileDirectory = System.getProperty("user.dir") + "/src/main/resources/TextFiles";

	// Assign the location of text files to the variable.
	private String modalFileDirectory = System.getProperty("user.dir") + "/src/main/resources/AiModal";

	@Override
	public double[] getAveragePolicyVector(Word2Vec model, File policyFile) throws IOException {
		List<double[]> vectors = new ArrayList<>();
		SentenceIterator iterator = new FileSentenceIterator(policyFile);

		while (iterator.hasNext()) {
			String sentence = iterator.nextSentence();
			String[] tokens = sentence.split(" ");
			double[] sentenceVector = new double[model.getLayerSize()];

			for (String token : tokens) {
				if (model.hasWord(token)) {
					double[] wordVector = model.getWordVector(token);
					for (int i = 0; i < sentenceVector.length; i++) {
						sentenceVector[i] += wordVector[i];
					}
				}
			}
			// Normalize sentence vector
			for (int i = 0; i < sentenceVector.length; i++) {
				sentenceVector[i] /= tokens.length; // Average across tokens
			}
			vectors.add(sentenceVector);
		}

		// Average the sentence vectors to get the policy vector
		double[] avgPolicyVector = new double[model.getLayerSize()];
		for (double[] vec : vectors) {
			for (int i = 0; i < avgPolicyVector.length; i++) {
				avgPolicyVector[i] += vec[i];
			}
		}
		if (!vectors.isEmpty()) {
			for (int i = 0; i < avgPolicyVector.length; i++) {
				avgPolicyVector[i] /= vectors.size();
			}
		}

		return avgPolicyVector;
	}

	@Override
	public double cosineSimilarity(double[] vecA, double[] vecB) {
		double dotProduct = 0.0;
		double normA = 0.0;
		double normB = 0.0;

		for (int i = 0; i < vecA.length; i++) {
			dotProduct += vecA[i] * vecB[i];
			normA += Math.pow(vecA[i], 2);
			normB += Math.pow(vecB[i], 2);
		}

		if (normA == 0.0 || normB == 0.0) {
			return 0.0; // Handle the case when one of the vectors is zero
		}
		return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}

	@Override
	public void getSimilarityFiles(List<String> keywords) throws IOException {
		@SuppressWarnings("deprecation")
		Word2Vec model = WordVectorSerializer.readWord2Vec(new File(modalFileDirectory + "/word2vecModel.txt"));

		Map<File, Double> similarityMap = new HashMap<>();
		double[] queryVector = new double[model.getLayerSize()];
		int count = 0;

		// Aggregate vectors for the keywords
		for (String keyword : keywords) {
			if (model.hasWord(keyword)) {
				double[] wordVector = model.getWordVector(keyword);
				for (int i = 0; i < queryVector.length; i++) {
					queryVector[i] += wordVector[i];
				}
				count++;
			}
		}

		// Normalize the query vector if any keywords were found
		if (count > 0) {
			for (int i = 0; i < queryVector.length; i++) {
				queryVector[i] /= count;
			}
		}

		File[] policyFiles = new File(fileDirectory).listFiles();
		if (policyFiles != null) {
			for (File policyFile : policyFiles) {
				double[] policyVector = getAveragePolicyVector(model, policyFile);
				double similarity = cosineSimilarity(queryVector, policyVector);
				if(similarity >= 0.75 ){
					similarityMap.put(policyFile, similarity);
				}	
			}
		}

		// Sort and retrieve top 5 similar policies
        List<File> relevantFiles = similarityMap.entrySet().stream()
            .sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()))
            .limit(5)
            .map(Map.Entry::getKey)
            .toList();
        
        // Read content from relevant files
        for (File file : relevantFiles) {
        	System.out.println();
            System.out.println("Content from: " + file.getName());
            extractContent(file, keywords);
        }

	}

	@Override
	public void extractContent(File file, List<String> keywords) throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                for (String keyword : keywords) {
                    if (line.toLowerCase().contains(keyword.toLowerCase())) {
                    	System.out.println(keyword);
                        System.out.println("Found keyword in line: " + line);
                        break; // Move to the next line once a keyword is found
                    }
                }
            }
        }
	}

}
