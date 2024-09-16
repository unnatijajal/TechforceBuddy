package com.techforcebuddybl.services.impl;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.techforcebuddybl.services.RawDataProcessingService;
import com.techforcebuddybl.util.ConstantData;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;

@Service
public class RawDataProcessingServiceImpl implements RawDataProcessingService {
	// Helper method to check if the POS tag is a noun or verb
	private boolean isNounOrVerb(String posTag) {
		return posTag.startsWith("NN") || posTag.startsWith("VB") || posTag.startsWith("JJ");
	}

	private Map<String, Set<String>> compareKeyword(List<String> extractedWords) {
		Map<String, Set<String>> matches = new HashMap<String, Set<String>>();
		Set<String> foundMatches = new HashSet<String>();
		
		Set<String> relevantPolicies = new HashSet<>();
		for (String word : extractedWords) {
		    for (String policyName : ConstantData.policyKeywords.keySet()) {
		        if (policyName.equalsIgnoreCase(word)) {
		            relevantPolicies.add(policyName);
		        }
		    }
		}

		for (String policyName : relevantPolicies) {
		    for (Map.Entry<String, String[]> entry : ConstantData.policyKeywords.entrySet()) {
		        if (entry.getKey().equalsIgnoreCase(policyName)) {
		            String[] values = entry.getValue();
		            for (String value : values) {
		                for (String word : extractedWords) {
		                    if (value.equalsIgnoreCase(word)) {
		                        foundMatches.add(value);
		                        matches.put(policyName, foundMatches);
		                    }
		                }
		            }
		        }
		    }
		}
		return matches;

	}

	@Override
	public Map<String, Set<String>> divideSentenceIntoWords(String data) throws Exception {
		
		try {
			FileInputStream inputStream = new FileInputStream("C:\\OpenNLP_models\\en-pos-maxent.bin");
			POSModel model = new POSModel(inputStream);
			POSTaggerME tagger = new POSTaggerME(model);

			WhitespaceTokenizer tokenizer = WhitespaceTokenizer.INSTANCE;
			String[] tokens = tokenizer.tokenize(data);

			String[] tags = tagger.tag(tokens);

			List<String> extractedWord = new ArrayList<String>();

			// Extract only noun and verbs
			for (int i = 0; i < tokens.length; i++) {
				String token = tokens[i];
				String nounTag = tags[i];
				if (isNounOrVerb(nounTag)) {
					extractedWord.add(token);
				}
			}

			return compareKeyword(extractedWord);

		} catch (Exception e) {
			throw e;
		}
	}

}
