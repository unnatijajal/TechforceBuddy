package com.techforcebuddybl.services.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techforcebuddybl.exception.DataNotFoundException;
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
	public Map<String, List<String>> getSimilarityFiles(List<String> keywords)
			throws IOException, DataNotFoundException {
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

		return extractContentAcrossFiles(relevantFiles, keywords);
	}

	public Map<String, List<String>> extractContentAcrossFiles(List<File> files, List<String> keywords)
			throws IOException, DataNotFoundException {
		
		int globalMaxKeywordCount = 0; // Maximum keyword count across all files
		String correspondingFileName = null; // Track which file had the max keyword count
		List<String> foundLines = new ArrayList<String>();
		boolean flag = false;
		for (File file : files) {

			// Create the document to load the file.
			PDDocument document = Loader.loadPDF(file);

			// Create the object of PDFTextStripper which is help to extract the data from
			// the pdf.
			PDFTextStripper textStripper = new PDFTextStripper();

			// Set the staring page the extract the data.
			textStripper.setStartPage(3);

			// Get the text from the pdf file.
			String text = textStripper.getText(document);
			;
			// Split content into paragraphs
			List<String> paragraphs = splitIntoParagraphs(text.toString());

			// Find the paragraph and line with the maximum number of keyword matches
			for (String paragraph : paragraphs) {
				List<String> lines = splitIntoLines(paragraph);
				flag =false;
				for (String line : lines) {
					int keywordCount = 0;
					
					// Check how many keywords the line contains
					for (String keyword : keywords) {
						if (line.toLowerCase().contains(keyword.toLowerCase())) {
							keywordCount++;
						}
					}

					// Update the global max if this line has more keywords than the current global
					// max
					if (keywordCount >= globalMaxKeywordCount) {
						globalMaxKeywordCount = keywordCount;
						flag = true;
						correspondingFileName = file.getName();
					}
				}
				if(flag) {
					foundLines.add(paragraph);
				}
			}
		}
		Map<String, List<String>> responseData = new HashMap<String, List<String>>();
		if (!foundLines.isEmpty()) {
			foundLines = filterResponse(foundLines);
			responseData.put(correspondingFileName, foundLines);
		} else {
			throw new DataNotFoundException("No Data found");

		}
		
		return responseData;
	}

	/*
	 * This method splits the content into paragraphs.
	 */
	public List<String> splitIntoParagraphs(String content) {
		List<String> paragraphs = new ArrayList<>();
		String[] paragraphArray = content.split("(?<!\\d\\.)\\.(?!\\d)"); // Split by period
		for (String paragraph : paragraphArray) {
			paragraphs.add(paragraph.trim());
		}
		return paragraphs;
	}

	/*
	 * This method splits a paragraph into individual lines.
	 */
	public List<String> splitIntoLines(String paragraph) {
		List<String> lines = new ArrayList<>();
		String[] lineArray = paragraph.split("\\r?\\n"); // Split by new lines
		for (String line : lineArray) {
			lines.add(line.trim());
		}
		return lines;
	}

	public List<String> filterResponse(List<String> responseData) {
		responseData.removeIf(
				s -> s.matches("\\d+(\\.\\d+)? .*") || 
				s.trim().split("\\s+").length < 4);
        // Print the modified text
        System.out.println(responseData);
		return responseData;
	}
}
