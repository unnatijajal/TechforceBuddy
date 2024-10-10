
package com.techforcebuddybl.services.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.stereotype.Service;

import com.techforcebuddybl.services.SentinizeAnalyzerService;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

@Service
public class SentinizeAnalyzerServiceImpl implements SentinizeAnalyzerService {
	private static File stanfordPropertiesFile = new File(System.getProperty("user.dir") + "/src/main/resources",
			"standford-corenlp.properties");
	 
	private final StanfordCoreNLP pipeline;
	 
	public SentinizeAnalyzerServiceImpl() throws FileNotFoundException, IOException {
		 // Initialize the Stanford NLP pipeline
        Properties properities = new Properties();
        properities.load(new FileInputStream(stanfordPropertiesFile));
        pipeline = new StanfordCoreNLP(properities);
	}


	// Method to analyze sentiment for sections
    public Map<String, String> analyzeSentiment(String[] sections) {
        Map<String, String> sentimentMap = new HashMap<>();
        for (String section : sections) {
            CoreDocument document = new CoreDocument(section);
            pipeline.annotate(document);

            // Get the sentiment for the first sentence of the document
            String sentiment = document.sentences().get(0).sentiment();
            sentimentMap.put(section, sentiment);
        }
        return sentimentMap;
    }

    // Method to analyze sentiment for a single query
    public String analyzeSentiment(String query) {
        CoreDocument document = new CoreDocument(query);
        pipeline.annotate(document);

        // Get sentiment for the query (assuming it's one sentence)
        return document.sentences().get(0).sentiment();
    }
    
	// Method to retrieve relevant content based on user query
	public Map<String, Double> retrieveContent(String userQuery,Map<String,Double> relaventSections) throws FileNotFoundException, IOException {
		 // Perform sentiment analysis on the sections
        Map<String, String> sectionSentimentScores = analyzeSentiment(relaventSections.keySet().toArray(new String[0]));
		
		// Analyze the sentiment of the user query
		String querySentiment = analyzeSentiment(userQuery);


		// Adjust final scores based on sentiment match
        for (Map.Entry<String, String> entry : sectionSentimentScores.entrySet()) {
            String section = entry.getKey();
            String sectionSentiment = entry.getValue();
            double score = relaventSections.get(section);

            // Adjust scores based on sentiment comparison
            if (querySentiment.equals(sectionSentiment)) {
            	relaventSections.put(section, score + 0.2); // Boost for matching sentiment
            } else if (!querySentiment.equals("Neutral") && !sectionSentiment.equals("Neutral")) {
            	relaventSections.put(section, score - 0.2); // Penalize for mismatching sentiments
            }
        }

        for (Map.Entry<String, Double> entry : relaventSections.entrySet()) {
            System.out.println(entry.getKey() + "\n" + entry.getValue());
        }

		return relaventSections;
	}

	
}
