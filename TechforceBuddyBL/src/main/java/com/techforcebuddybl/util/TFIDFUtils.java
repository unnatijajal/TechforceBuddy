package com.techforcebuddybl.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

/*
 * This is the utility class to compute the TF-IDF Of the data
 */

@Component
public class TFIDFUtils {
	
	// Store TF for each document
    private static Map<String, Map<String, Double>> documentTFMap = new HashMap<>();

    // Store IDF of all documents
    public static Map<String, Double> idfMap = new HashMap<>();
    
    // This is method to compute the Term Frequency (TF)
    public static void computeTF(String fileName,String[] tokens) {
        Map<String, Double> tfMap = new HashMap<>();
        double totalTokens = tokens.length;
        
        for (String token : tokens) {
            tfMap.put(token, tfMap.getOrDefault(token, 0.0) + 1.0);
        }
        
        // Normalize by dividing by total number of tokens
        for (String token : tfMap.keySet()) {
            tfMap.put(token, tfMap.get(token) / totalTokens);
        }
        
     // Store TF for this document
        documentTFMap.put(fileName, tfMap);
    }
    
    // This is method to compute the Inverse Document Frequency (IDF)
    public static void computeIDF(List<String[]> documents) { 
    	  	
        double totalDocs = documents.size();
        for (String[] document : documents) {
            for (String token : document) {
                idfMap.put(token, idfMap.getOrDefault(token, 0.0) + 1.0);
            }
        }
        
        // Calculate IDF using log(totalDocs / docFreq)
        for (String token : idfMap.keySet()) {
            idfMap.put(token, Math.log(totalDocs / idfMap.get(token)));
        }

    }
    
    // Retrieve TF for a given document and term
    public double getTF(String documentName, String term) {
        Map<String, Double> tfMap = documentTFMap.get(documentName);
        if (tfMap != null) {
            return tfMap.getOrDefault(term, 0.0);
        }
        return 0.0;
    }
    
    // Method to calculate the TF-IDF vector for a query
    public static double[] getQueryVector(List<String> queryTokens) {
        double[] queryVector = new double[idfMap.size()];
        List<String> allTokens = new ArrayList<>(idfMap.keySet()); // Get all unique terms from the documents

        // Calculate TF for query terms (you can adjust this based on actual TF calculation for the query)
        Map<String, Double> queryTF = new HashMap<>();
        for (String token : queryTokens) {
            queryTF.put(token, queryTF.getOrDefault(token, 0.0) + 1.0); // Raw term frequency (count)
        }

        // Build the query vector using TF-IDF for each term
        for (int i = 0; i < allTokens.size(); i++) {
            String term = allTokens.get(i);
            double tf = queryTF.getOrDefault(term, 0.0);
            double idf = idfMap.getOrDefault(term, 0.0);

            // Query vector element = TF * IDF
            queryVector[i] = tf * idf;
        }

        return queryVector;
    }

}
