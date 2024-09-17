package com.techforcebuddybl.services.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techforcebuddybl.services.UserDataProcessingService;
import com.techforcebuddybl.util.ConstantData;

@Service
public class UserDataProcessingServiceImpl implements UserDataProcessingService {

	@Autowired
	DataParsingServiceImpl parsingServiceImpl;

	String[] tokens;
	String[] tags;
	
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
		System.out.println(matches);
		return matches;

	}

	@Override
	public Map<String, Set<String>> divideSentenceIntoWords(String data) throws Exception {

		try {
			tokens = parsingServiceImpl.tokenizeData(data);
			tags = parsingServiceImpl.parsingData(tokens);
			List<String> extractedWord = new ArrayList<String>();

			// Extract only noun and verbs
			for (int i = 0; i < tokens.length; i++) {
				String token = tokens[i];
				String nounTag = tags[i];
				if (parsingServiceImpl.isNounOrVerb(nounTag)) {
					extractedWord.add(token);
				}
			}
	
			searchKeywordsInFiles(compareKeyword(extractedWord));
			return compareKeyword(extractedWord);

		} catch (Exception e) {
			throw e;
		}
	}

	public void searchKeywordsInFiles(Map<String, Set<String>> matches) throws FileNotFoundException, IOException {
		String[] searchedContent;

		for (Map.Entry<String, Set<String>> entry : matches.entrySet()) {
			searchedContent = readFile(entry.getKey(),entry.getValue());
		}
	}

	private String[] readFile(String key,Set<String> value) throws FileNotFoundException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
	    URL resource = classLoader.getResource("static/TextFiles/" + key + ".txt");
	    File file = new File(resource.getFile());
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				tokens = parsingServiceImpl.tokenizeData(line);
				for(String token : tokens) {
					for(String word : value) {
						if(word.equalsIgnoreCase(token)) {
							System.out.println(key + " "+word+",");
						}
					}
				}
			}
		}
		return null;
	}

}
