package com.techforcebuddybl.services.impl;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TFIDFSimilarity {
	
	public List<String> searchRelevantSections(List<String> keywords) throws IOException, ParseException {
	    // Path to your JSON file
	    String jsonFilePath = System.getProperty("user.dir") + "/src/main/resources/json/output.json";


	    // Step 1: Parse the JSON file
	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode rootNode = mapper.readTree(new File(jsonFilePath));

	    // Step 2: Create a Lucene index
	    Directory directory = new ByteBuffersDirectory();
	    IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
	    IndexWriter writer = new IndexWriter(directory, config);

	    // Step 3: Iterate through the policies in the JSON and index them
	    Iterator<String> filesName = rootNode.fieldNames();
	    while (filesName.hasNext()) {
	        String fileName = filesName.next();
	        JsonNode sectionArray = rootNode.get(fileName);

	        // Loop through each section in the document
	        for (JsonNode sectionNode : sectionArray) {
	            String heading = sectionNode.get("heading").asText();
	            String section = sectionNode.get("section").asText();
	            String subheading = sectionNode.get("subheading").asText();

	            // Create a Lucene document and add fields
	            Document doc = new Document();
	            doc.add(new TextField("heading", heading, Field.Store.YES));
	            doc.add(new TextField("section", section, Field.Store.YES));
	            doc.add(new TextField("subheading", subheading, Field.Store.YES));
	            doc.add(new StringField("fileName", fileName, Field.Store.YES));
	            // Add the document to the index
	            writer.addDocument(doc);
	        }
	    }
	    writer.close();

	    // Step 4: Query the index using TF-IDF similarity
	    DirectoryReader reader = DirectoryReader.open(directory);
	    IndexSearcher searcher = new IndexSearcher(reader);
	    
	    // Declare and instantiate the QueryParser
	    QueryParser parser = new QueryParser("heading", new StandardAnalyzer());

	    // Construct a query for multi-word phrases
	    StringBuilder queryText = new StringBuilder();
	    for (String keyword : keywords) {
	        queryText.append("\"").append(keyword).append("\"").append(" "); // Use quotes for phrase search
	    }
	    
	    // Search in all fields (heading, subheading, section)
	    String combinedQuery = String.format("heading:(%s) OR subheading:(%s) OR section:(%s)", 
	                                          queryText.toString(), queryText.toString(), queryText.toString());
	   
	    // Parse the combined query
	    Query query = parser.parse(combinedQuery);
	    // Step 5: Search and display results
	    TopDocs results = searcher.search(query, 5);
	    List<String> relevantSection = new ArrayList<String>();
	    for (ScoreDoc scoreDoc : results.scoreDocs) {
	        Document doc = searcher.doc(scoreDoc.doc);
	        String heading = doc.get("heading");
	        String section = doc.get("section");
	        String subheading = doc.get("subheading");
	       // float score = scoreDoc.score;
	        relevantSection.add(heading+"\n"+subheading+"\n"+section);
	    }
	    
	    return relevantSection;
	}



}
