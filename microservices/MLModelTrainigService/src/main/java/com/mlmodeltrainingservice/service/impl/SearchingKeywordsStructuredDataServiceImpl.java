package com.mlmodeltrainingservice.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.mlmodeltrainingservice.service.SearchingKeywordsStructuredDataService;
import com.mlmodeltrainingservice.service.TFIDFSimilarity;


@Service
public class SearchingKeywordsStructuredDataServiceImpl implements SearchingKeywordsStructuredDataService{

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private TFIDFSimilarity tfidfSimilarity;
	
	
	@Override
	public LinkedHashMap<String, List<String>> getResponsUsingStructuredData(String query) throws Exception {
		
		@SuppressWarnings("deprecation")
		Word2Vec word2Vec = WordVectorSerializer.readWord2Vec(
				new File(System.getProperty("user.dir") + "/src/main/resources/AiModel/word2vecModel.bin"));

		List<String> extractedWord = restTemplate.postForObject("http://192.168.1.214:8084/processQuery?query=" + query,
				null, List.class);
		
		Map<String, List<String>> relevantSection;

		relevantSection = tfidfSimilarity.searchRelevantSections(extractedWord);

		LinkedHashMap<String, List<String>> answer;

		answer = refineWithWord2Vec(word2Vec, relevantSection, extractedWord);

		LinkedHashMap<String, List<String>> finalResult = answer.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, // Keep the original key
						entry -> entry.getValue().stream().limit(2).collect(Collectors.toList()), // Limit the list to 2
																									// elements
						(existing, replacement) -> existing, // Merge function in case of duplicate keys
						LinkedHashMap::new // Use LinkedHashMap as the map type
				));

		LinkedHashMap<String, List<String>> formattedResults = formatRefinedResults(finalResult);
		return formattedResults;
	}

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
	public LinkedHashMap<String, List<String>> refineWithWord2Vec(Word2Vec word2Vec, Map<String, List<String>> luceneResults,
			List<String> queryKeywords) {
		INDArray queryVector = getQueryVector(queryKeywords, word2Vec);
	    queryVector = normalizeVector(queryVector);

	    LinkedHashMap<String, List<String>> refinedResults = new LinkedHashMap<>();

	    for (Map.Entry<String, List<String>> entry : luceneResults.entrySet()) {
	        String fileName = entry.getKey();
	        List<String> sections = entry.getValue();

	        Map<String, SectionData> sectionDataMap = new LinkedHashMap<>();

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

	        List<String> finalResults = new ArrayList<String>();
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
	                    finalResults.add(resultEntry.getKey());
	                }
	            });

	        refinedResults.put(fileName, finalResults); // Store results by file
	    }

	    return refinedResults;
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
	    public SectionData(double similarity, int keywordCount, boolean containsFirstKeyword){
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
	
	public static LinkedHashMap<String, List<String>> formatRefinedResults(
			LinkedHashMap<String, List<String>> refinedResults) {
		LinkedHashMap<String, List<String>> formattedResults = new LinkedHashMap<>();

		for (Map.Entry<String, List<String>> entry : refinedResults.entrySet()) {
			String fileName = entry.getKey();
			List<String> sections = entry.getValue();
			List<String> formattedSections = new ArrayList<>();

			for (String section : sections) {
				String[] parts = section.split("\n", 3); // Split into heading, subheading, and section

				String formattedHeading = parts.length > 0 ? boldText(parts[0].toUpperCase()) : "";
				String formattedSubheading = parts.length > 1 ? toTitleCase(parts[1]) : "";
				String formattedSection = parts.length > 2 ? formatSectionWithBullets(parts[2]) : "";

				// Add the formatted content to the list
				formattedSections.add(formattedHeading);
				if (!formattedSubheading.isEmpty()) {
					formattedSections.add(formattedSubheading);
				}
				formattedSections.add(formattedSection);

			}

			// Store the formatted sections in the result map
			formattedResults.put(fileName, formattedSections);
		}

		return formattedResults;
	}

	// Make text bold using HTML tags
	public static String boldText(String text) {
		return "<strong>" + text + "</strong>"; // Use HTML <strong> tags for bold text
	}

	// Convert a string to title case
	public static String toTitleCase(String input) {
		if (input == null || input.isEmpty()) {
			return input;
		}
		String[] words = input.split(" ");
		StringBuilder titleCase = new StringBuilder();

		for (String word : words) {
			if (word.length() > 1) {
				titleCase.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase());
			} else {
				titleCase.append(word.toUpperCase());
			}
			titleCase.append(" ");
		}
		return titleCase.toString().trim();
	}

	// Format the section content with bullet points based on complete sentences
	public static String formatSectionWithBullets(String section) {
		StringBuilder formattedSection = new StringBuilder();
		StringBuilder currentSentence = new StringBuilder();

		// Split the section into sentences based on full stops followed by spaces
		String[] sentences = section.split("(?<=\\.)\\s*"); // Adjust regex to split by full stop

		for (String sentence : sentences) {
			// Replace newlines with spaces if they do not precede a period
			sentence = sentence.replaceAll("\n(?!\\s*\\.)", " ");

			if (!sentence.trim().isEmpty()) {
				currentSentence.append(sentence.trim());

				// Check if the current sentence ends with a full stop
				if (currentSentence.toString().endsWith(".")) {
					// Only add a bullet if the formatted section does not already start with a
					// bullet
					if (!formattedSection.toString().trim().startsWith("•")) {
						formattedSection.append("• ").append(currentSentence.toString().trim()).append("\n");
					} else {
						// Append the current sentence without adding another bullet
						formattedSection.append(currentSentence.toString().trim()).append("\n");
					}
					currentSentence.setLength(0); // Reset currentSentence for the next sentence
				}
			}
		}

		// If there are any remaining sentences not ending with a full stop
		if (currentSentence.length() > 0) {
			// Only add a bullet if the formatted section does not already start with a
			// bullet
			if (!formattedSection.toString().trim().startsWith("•")) {
				formattedSection.append("• ").append(currentSentence.toString().trim()).append("\n");
			} else {
				// Append the current sentence without adding another bullet
				formattedSection.append(currentSentence.toString().trim()).append("\n");
			}
		}

		return formattedSection.toString().trim();
	}

}
