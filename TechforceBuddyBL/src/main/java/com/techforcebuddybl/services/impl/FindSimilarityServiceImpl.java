package com.techforcebuddybl.services.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
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
	public List<File> getRelaventFiles(List<String> keywords) throws IOException, DataNotFoundException {
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
					if (tokens == null) {
						continue;
					}
					// Call the computeTFIDFWeightedWord2Vec method
					double[] documentVector = tfidfWord2VecService.computeTFIDFWeightedWord2Vec(policyFile.getName(),
							tokens);
					// Invoke the cosineSimilarity() to calculate the similarities.
					double similarity = cosineSimilarity(queryVector, documentVector);
					// Store the similarity file only if the similarity is greater than the 0.69
					if (similarity > 0.50) {
						similarityMap.put(policyFile, similarity);
					}
				}
			}
		}

		// Sort and retrieve top 5 similar policies
		List<File> relevantFiles = similarityMap.entrySet().stream()
				.sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue())).limit(5)
				.map(Map.Entry::getKey).toList();

		return relevantFiles;
	}

	/*
	 * public Map<String, List<String>> getTheResponseFromRelaventFile(List<File>
	 * files, List<String> keywords) throws IOException, DataNotFoundException {
	 * 
	 * int globalMaxKeywordCount = keywords.size()/2; // Maximum keyword count
	 * across all files String correspondingFileName = null; // Track which file had
	 * the max keyword count List<String> foundLines = new ArrayList<String>();
	 * boolean flag = false;
	 * 
	 * Map<String,Integer> linesKeywords = new HashMap<String, Integer>();
	 * 
	 * for (File file : files) {
	 * 
	 * // Create the document to load the file. PDDocument document =
	 * PDDocument.load(file);
	 * 
	 * // Create the object of PDFTextStripper which is help to extract the data
	 * from // the pdf. PDFTextStripper textStripper = new PDFTextStripper();
	 * 
	 * // Set the staring page the extract the data. textStripper.setStartPage(3);
	 * 
	 * // Get the text from the pdf file. String text =
	 * textStripper.getText(document);
	 * 
	 * // Split content into paragraphs List<String> paragraphs =
	 * splitIntoParagraphs(text.toString());
	 * 
	 * // Find the paragraph and line with the maximum number of keyword matches for
	 * (String paragraph : paragraphs) { List<String> lines =
	 * splitIntoLines(paragraph); flag =false; for (String line : lines) { int
	 * keywordCount = 0;
	 * 
	 * // Check how many keywords the line contains for (String keyword : keywords)
	 * { if (line.toLowerCase().contains(keyword.toLowerCase())) { keywordCount++; }
	 * }
	 * 
	 * // Update the global max if this line has more keywords than the current
	 * global // max if (keywordCount > globalMaxKeywordCount) {
	 * globalMaxKeywordCount = keywordCount; flag = true; correspondingFileName =
	 * file.getName(); } } if(flag) { linesKeywords.put(paragraph,
	 * globalMaxKeywordCount); //paragraph = filterParagraph(paragraph);
	 * foundLines.add(paragraph); } } }
	 * 
	 * List<String> relaventParagraphs = linesKeywords.entrySet().stream()
	 * .sorted((entryOne,entryTwo)->Double.compare(entryTwo.getValue(),
	 * entryOne.getValue())) .limit(2) .map(Map.Entry::getKey) .toList();
	 * 
	 * List<String> filteredParagrap = new ArrayList<String>(); for(String para :
	 * relaventParagraphs) { filteredParagrap.add(filterParagraph(para)); }
	 * Map<String, List<String>> responseData = new HashMap<String, List<String>>();
	 * if (!foundLines.isEmpty()) { responseData.put(correspondingFileName,
	 * filteredParagrap); //responseData.put(correspondingFileName, foundLines); }
	 * else { throw new DataNotFoundException("No Data found");
	 * 
	 * }
	 * 
	 * return responseData; }
	 */

	public Map<String, List<String>> getTheResponseFromRelaventFile(List<File> files, List<String> keywords)
			throws IOException, DataNotFoundException {

		String correspondingFileName = null; // Track which file had the paragraph with all keywords
		List<String> foundParagraphs = new ArrayList<>();

		// Store paragraphs that match all keywords
		Map<String, Integer> paragraphsKeywords = new HashMap<>();
		File resourceDir = new File(System.getProperty("user.dir") + "/src/main/resources/pdf");
		String[] allFiles = resourceDir.list();

		// Iterate over the files
		for (String fileName : allFiles) {

			// Check if the file is a PDF file
			if (fileName.endsWith(".pdf")) {
				// Get the file
				File f = new File(resourceDir, fileName);
				// Load the document
				PDDocument document = PDDocument.load(f);

				// Create PDFTextStripper object to extract text
				PDFTextStripper textStripper = new PDFTextStripper();
				textStripper.setStartPage(3);

				// Extract text from the PDF file
				String text = textStripper.getText(document);
				text = getContentAfterRemoveFooter(document, fileName).toString();
				// Split content into paragraphs
				List<String> paragraphs = splitIntoParagraphs(text.toString());

				// Find paragraphs that contain all keywords
				for (String paragraph : paragraphs) {

					// Check if all keywords are present in the paragraph
					boolean allKeywordsPresent = keywords.stream()
							.allMatch(keyword -> paragraph.toLowerCase().contains(keyword.toLowerCase()));

					// If the paragraph contains all keywords, store it
					if (allKeywordsPresent) {
						correspondingFileName = f.getName();
						paragraphsKeywords.put(paragraph, keywords.size()); // Use total keyword count as the value
						foundParagraphs.add(paragraph);
					}
				}
			}
		}

		// Collect the paragraphs that matched all keywords
		List<String> relevantParagraphs = paragraphsKeywords.entrySet().stream()
				.sorted((entryOne, entryTwo) -> Double.compare(entryTwo.getValue(), entryOne.getValue()))
				.limit(2) 																									
				.map(Map.Entry::getKey).toList();

		// Filter the relevant paragraphs (optional, based on your filterParagraph
		// method)
		List<String> filteredParagraphs = new ArrayList<>();
		for (String para : relevantParagraphs) {
			filteredParagraphs.add(filterParagraph(para));
		}

		// Prepare the final response data
		Map<String, List<String>> responseData = new HashMap<>();
		if (!foundParagraphs.isEmpty()) {
			responseData.put(correspondingFileName, filteredParagraphs);
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
		String[] paragraphArray = content.split("(?m)(\\n{2,}|\\n\\d\\.)"); // Split by period
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

	public String filterParagraph(String responseData) {
		responseData = responseData.replaceAll("\\b\\d+\\.\\d+\\b", "");
		responseData = responseData.split("\\s").length < 4 ? "" : responseData;

		return responseData;
	}
	
	public StringBuilder getContentAfterRemoveFooter
			(PDDocument document, String fileName) throws IOException {
	    // PDFTextStripper to extract the text from each page
	    PDFTextStripper textStripper = new PDFTextStripper();
	    
	    // Iterate through the pages
	    int numberOfPages = document.getNumberOfPages();
	    StringBuilder modifiedContent = new StringBuilder();

	    for (int i = 2; i < numberOfPages; i++) {
	        PDPage page = document.getPage(i);

	        textStripper.setStartPage(i + 1);
	        textStripper.setEndPage(i + 1);

	        // Extract the text of the page
	        String pageText = textStripper.getText(document);

	        // Split the text into lines
            String[] lines = pageText.split("\\r?\\n");

            // Check if the page has more than two lines
            if (lines.length > 2) {
               
                for (int j = 0; j < lines.length - 2; j++) {
                    modifiedContent.append(lines[j]).append(System.lineSeparator());
                }
            }

	        
	    }
	    return modifiedContent;
	}
	
}
