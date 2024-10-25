package com.searchingservice.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.searchingservice.service.SearchingKeywordsService;

@Service
public class SearchingKeywordsServiceImpl implements SearchingKeywordsService {

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public Map<String, String> searchingKeywordsForUnstructuredData(String query) {
		List<String> queryKeywords = restTemplate.postForObject("http://localhost:8084/processQuery?query=" + query,
				null, List.class);
		Map<String, String> pdfsText = restTemplate.getForObject("http://localhost:8081/pdfExtraction/getContent",
				Map.class);

		LinkedHashMap<String, SectionData> relevantSections = new LinkedHashMap<>();
		for (Map.Entry<String, String> entry : pdfsText.entrySet()) {

			List<String> paragraphs = splitIntoParagraphs(entry.getValue().toString());

			for (String paragraph : paragraphs) {

				List<String> lines = splitIntoLines(paragraph);

				for (String line : lines) {
					String lowerCaseLine = line.toLowerCase();
					List<String> sentenceTokens = tokenize(lowerCaseLine);

					double totalSimilarity = 0.0;
					int keywordCount = 0;

					Set<String> matchedKeywords = new HashSet<>();
					boolean firstKeywordFound = false;

					for (String queryKeyword : queryKeywords) {
						if (wordExists(queryKeyword) && !matchedKeywords.contains(queryKeyword)) {
							for (String token : sentenceTokens) {
								if (wordExists(token)) {
									if (queryKeyword.equals(token)) {
										matchedKeywords.add(queryKeyword);
										if (!firstKeywordFound) {
											keywordCount += 3;
											firstKeywordFound = true;
										} else {
											keywordCount++;
										}
										break;
									}
									double[] queryVector = getWordVector(queryKeyword);
									double[] tokenVector = getWordVector(token);
									double similarity = cosineSimilarity(queryVector, tokenVector);
									totalSimilarity += similarity;
								}
							}
						}
					}

					if (keywordCount > 0) {
						double avgSimilarity = totalSimilarity / sentenceTokens.size();
						SectionData sectionData = new SectionData(paragraph, keywordCount, avgSimilarity,
								entry.getKey());
						relevantSections.put(line, sectionData);
					}
				}
			}
		}
		// Sort the sections first by keyword count, then by similarity
		List<Map.Entry<String, SectionData>> sortedSections = new ArrayList<>(relevantSections.entrySet());
		sortedSections.sort((e1, e2) -> {
			int keywordComparison = Integer.compare(e2.getValue().getKeywordCount(), e1.getValue().getKeywordCount());
			if (keywordComparison == 0) {
				return Double.compare(e2.getValue().getAvgSimilarity(), e1.getValue().getAvgSimilarity());
			} else {
				return keywordComparison;
			}
		});
		LinkedHashMap<String, String> finalResults = new LinkedHashMap<String, String>();
		// Extract the sorted relevant sections

		for (Map.Entry<String, SectionData> entry : sortedSections) {
			finalResults.put(entry.getValue().getSection(), entry.getValue().getFileName());
		}

		// Limit the size to 10 if necessary and return a LinkedHashMap
		if (finalResults.size() > 10) {
			return finalResults.entrySet().stream().limit(10)
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
							(existing, replacement) -> existing, // In case of duplicate keys, keep the existing entry
							LinkedHashMap::new // Use LinkedHashMap to maintain insertion order
					));
		}

		return finalResults;

	}

	@Override
	public void searchingKeywordsForStructuredData(List<String> queryKeywords) {
		// TODO Auto-generated method stub

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

	private double[] getWordVector(String word) {
		String url = "http://localhost:8083/word-vector/" + word;
		ResponseEntity<double[]> response = restTemplate.getForEntity(url, double[].class);
		return response.getBody();
	}

	private boolean wordExists(String word) {
		String url = "http://localhost:8083/word-vector/" + word;
		try {
			restTemplate.getForEntity(url, double[].class);
			return true;
		} catch (HttpClientErrorException e) {
			return false;
		}
	}
}
