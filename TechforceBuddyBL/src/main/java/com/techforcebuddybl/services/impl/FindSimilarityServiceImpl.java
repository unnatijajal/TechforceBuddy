package com.techforcebuddybl.services.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.springframework.stereotype.Service;

import com.techforcebuddybl.exception.DataNotFoundException;
import com.techforcebuddybl.services.FindSimilarityService;

/*
 * 
 * This is class which different methods which are used to find the 
 * similarities between the User's query vector and All document Vector
 * 
 */

@Service
public class FindSimilarityServiceImpl implements FindSimilarityService {

	// Path of directory of the pdf files
	private String fileDirectory = System.getProperty("user.dir") + "/src/main/resources/pdf";

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

	@SuppressWarnings("unchecked")
	public Map<String,String> getRelaventFilesResponse(List<String> queryKeywords)
			throws IOException, DataNotFoundException {
		@SuppressWarnings("deprecation")
		Word2Vec word2Vec = WordVectorSerializer.readWord2Vec(new File(modalFileDirectory + "/word2vecModel.txt"));

		// Get the all the policy files from the directory
		File[] policyFiles = new File(fileDirectory).listFiles();
		Map<String, SectionData> relevantSections = new HashMap<>();

		if (policyFiles != null) {

			for (File policyFile : policyFiles) {

				if (policyFile.isFile() && policyFile.getName().endsWith(".pdf")) {
					PDDocument document = PDDocument.load(policyFile);

					// Create the object of PDFTextStripper which is help to extract the data from the pdf.
					PDFTextStripper textStripper = new PDFTextStripper();
					
					// Set the staring page the extract the data.
					textStripper.setStartPage(3);
					
					// Get the text from the pdf file.
					String text = textStripper.getText(document);
					
					List<String> paragraphs = splitIntoParagraphs(text.toString());

					// Find the paragraph and line with the maximum number of keyword matches
					for (String paragraph : paragraphs) {
						List<String> lines = splitIntoLines(paragraph);
						
						for(String line: lines) {
							String lowerCaseLine = line.toLowerCase();
				            // Tokenize each line (representing a sentence or section)
				            List<String> sentenceTokens = tokenize(lowerCaseLine);

				            // Calculate similarity for each keyword in the query
				            double totalSimilarity = 0.0;
				            int keywordCount = 0;
				            
				            // Use a Set to ensure that each keyword is only counted once per section
				            Set<String> matchedKeywords = new HashSet<>();
				            boolean firstKeywordFound = false;  // To track if the first keyword has been found
				            
				            for (String queryKeyword : queryKeywords) {
				                if (word2Vec.hasWord(queryKeyword) && !matchedKeywords.contains(queryKeyword)) {
				                    for (String token : sentenceTokens) {
										if (word2Vec.hasWord(token)) {
											// Count keyword matches
											if (queryKeyword.equals(token)) {
												matchedKeywords.add(queryKeyword); // Mark the keyword as matched
												if (!firstKeywordFound) {
				                                    keywordCount += 3;  // Higher weight for the first keyword match
				                                    firstKeywordFound = true;
				                                } else {
				                                    keywordCount++;  // Normal weight for subsequent keywords
				                                }
												break; // Move to the next keyword after matching
											}
				                            double[] queryVector = word2Vec.getWordVector(queryKeyword);
				                            double[] tokenVector = word2Vec.getWordVector(token);
				                            double similarity = cosineSimilarity(queryVector, tokenVector);
				                            totalSimilarity += similarity;
				                        }
				                    }
				                }
				            }

							if (keywordCount > 0) {
								// Calculate average similarity for the section
								double avgSimilarity = totalSimilarity / sentenceTokens.size();
								SectionData sectionData = new SectionData(paragraph, keywordCount, avgSimilarity,policyFile.getName());
								 relevantSections.put(line, sectionData);
							}
				        }
					}
				}
			}
		}
		
		// Sort the sections first by keyword count, then by similarity
		List<Map.Entry<String, SectionData>> sortedSections = new ArrayList<>(relevantSections.entrySet());
		sortedSections.sort((e1, e2) -> {
			int keywordComparison = Integer
					.compare(e2.getValue().getKeywordCount(), e1.getValue().getKeywordCount());
			if (keywordComparison == 0) {
				return Double
						.compare(e2.getValue().getAvgSimilarity(), e1.getValue().getAvgSimilarity());
			} else {
				return keywordComparison;
			}
		});
		Map<String,String> finalResults = new HashMap<String, String>();
		// Extract the sorted relevant sections
       
        for (Map.Entry<String, SectionData> entry : sortedSections) {
            finalResults.put(entry.getValue().getSection(), entry.getValue().getFileName());
        }
        if(finalResults.size()>10) {
        	return (Map<String, String>) finalResults.entrySet().stream().limit(10);
        }
        return finalResults;
	}

	// Tokenizer function to tokenize sentences
	private List<String> tokenize(String text) {
		DefaultTokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
		return tokenizerFactory.create(text).getTokens();
	}

	private class SectionData {
		private String section;
		private int keywordCount;
		private double avgSimilarity;
		private String fileName;

		public SectionData(String section, int keywordCount, double avgSimilarity, String fileName) {
			this.section = section;
			this.keywordCount = keywordCount;
			this.avgSimilarity = avgSimilarity;
			this.fileName = fileName;
		}

		public int getKeywordCount() {
			return keywordCount;
		}

		public double getAvgSimilarity() {
			return avgSimilarity;
		}

		public String getSection() {
			return section;
		}

		public String getFileName() {
			return fileName;
		}
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

	public String filterParagraph(String responseData) {
		responseData = responseData.replaceAll("\\b\\d+\\.\\d+\\b", "");
		responseData = responseData.split("\\s").length < 4 ? "" : responseData;
		responseData = responseData.replaceAll("^\\d+\\s*\\|\\s*P\\s*a\\s*g\\s*e", "");
		return responseData;
	}
}
