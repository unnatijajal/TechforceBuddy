
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


    // Method to analyze sentiment for a single query
    public String analyzeSentiment(String query) {
        CoreDocument document = new CoreDocument(query);
        pipeline.annotate(document);

        // Get sentiment for the query (assuming it's one sentence)
        return document.sentences().get(0).sentiment();
    }
    
	

	
}
