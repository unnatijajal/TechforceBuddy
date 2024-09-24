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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techforcebuddybl.services.FindSimilarityService;
import com.techforcebuddybl.services.TFIDFWord2VecService;

/*
 * 
 * This is class which different methods which are used to find the 
 * similarities between the User's query vector and All document Vector
 * 
 */

@Service
public class FindSimilarityServiceImpl implements FindSimilarityService {

	@Autowired
	private TFIDFWord2VecService tfidfWord2VecService;

	// Path of directory of the pdf files
	private String fileDirectory = System.getProperty("user.dir") + "/src/main/resources/pdf";

	// Path of directory of the text files
	private String textFileDirectory = System.getProperty("user.dir") + "/src/main/resources/TextFiles/";

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

	// This is method to find the similarities between the User's query vector and
	// All document Vector
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

	/*
	 * This is the method which find the relative similarities file which content is
	 * relative to the user query.
	 */

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

		// Get the all the policy files from the directory
		File[] policyFiles = new File(fileDirectory).listFiles();

		if (policyFiles != null) {

			for (File policyFile : policyFiles) {

				if (policyFile.isFile() && policyFile.getName().endsWith(".pdf")) {

					// Get the tokens for the current document
					String[] tokens = ExtractDataFromPdfServiceImpl.documentMapToken.get(policyFile.getName());

					// Call the computeTFIDFWeightedWord2Vec method
					double[] documentVector = tfidfWord2VecService.computeTFIDFWeightedWord2Vec(policyFile.getName(),
							tokens);
					// Invoke the cosineSimilarity() to calculate the similarities.
					double similarity = cosineSimilarity(queryVector, documentVector);

					// Store the similarity file only if the similarity is greater than the 0.69
					if (similarity >= 0.70) {
						similarityMap.put(policyFile, similarity);
					}
				}
			}
		}

		// Sort and retrieve top 5 similar policies
		List<File> relevantFiles = similarityMap.entrySet().stream()
				.sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue())).limit(5)
				.map(Map.Entry::getKey).toList();

		// Read content from relevant files
		for (File file : relevantFiles) {
			System.out.println("Content from: " + file.getName());

			// Invoke the method to extract the content from the text file.
			extractContent(file, keywords);
		}

	}

	/*
	 * This is the method to extract the content from the relative files
	 */
	@Override
	public void extractContent(File file, List<String> keywords) throws IOException {
		StringBuilder fileContent = new StringBuilder();

		// Read entire file content
		try (BufferedReader reader = new BufferedReader(new FileReader(
				textFileDirectory + file.getName().substring(0, file.getName().lastIndexOf(".")) + ".txt"))) {
			String line;
			while ((line = reader.readLine()) != null) {
				fileContent.append(line).append(" ");
			}
		}

		// Split content into sentences (or paragraphs if preferred)
		List<String> paraghaphs = splitIntoParagraphs(fileContent.toString());

		// Process each sentence to find relevant ones
		for (String paragraph : paraghaphs) {
			for (String keyword : keywords) {
				if (paragraph.toLowerCase().contains(keyword.toLowerCase())) {
					System.out.println("Found keyword: " + keyword);
					System.out.println("Relevant sentence: "
							+ paragraph.replaceAll("(?i)" + keyword, "\033[43m\033[91m" + keyword + "\033[0m"));
					break; // Move to the next sentence once a keyword is found
				}
			}
		}
	}

	/*
	 * This method split the given content when full stop is found and 
	 * store it into the list.
	 */
	public List<String> splitIntoParagraphs(String content) {
		List<String> paragraphs = new ArrayList<>();
		String[] paragraphArray = content.split("\\.");
		for (String paragraph : paragraphArray) {
			paragraphs.add(paragraph.trim());
		}
		return paragraphs;
	}

}
