package com.techforcebuddybl.services.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techforcebuddybl.services.UserDataProcessingService;

@Service
public class UserDataProcessingServiceImpl implements UserDataProcessingService {

	@Autowired
	DataParsingServiceImpl parsingServiceImpl;

	@Autowired
	private DataParsingServiceImpl dataParsingServiceImpl;

	@Autowired
	private FindSimilarityServiceImpl similarityServiceImpl;
	
	String[] tokens;
	String[] tags;

	/*
	 * private Map<String, Set<String>> compareKeyword(List<String> extractedWords)
	 * { Map<String, Set<String>> matches = new HashMap<String, Set<String>>();
	 * Set<String> foundMatches = new HashSet<String>();
	 * 
	 * Set<String> relevantPolicies = new HashSet<>(); for (String word :
	 * extractedWords) { for (String policyName :
	 * ConstantData.policyKeywords.keySet()) { if
	 * (policyName.equalsIgnoreCase(word)) { relevantPolicies.add(policyName); } } }
	 * 
	 * for (String policyName : relevantPolicies) { for (Map.Entry<String, String[]>
	 * entry : ConstantData.policyKeywords.entrySet()) { if
	 * (entry.getKey().equalsIgnoreCase(policyName)) { String[] values =
	 * entry.getValue(); for (String value : values) { for (String word :
	 * extractedWords) { if (value.equalsIgnoreCase(word)) {
	 * foundMatches.add(value); matches.put(policyName, foundMatches); } } } } } }
	 * System.out.println(matches); return matches;
	 * 
	 * }
	 */
	@Override
	public Map<String, Set<String>> divideSentenceIntoWords(String data) throws Exception {
		try {
			tokens = parsingServiceImpl.tokenizeData(data);
			tags = parsingServiceImpl.parsingData(tokens);

			tokens = dataParsingServiceImpl.removeWordStop(tokens);
			//tokens = dataParsingServiceImpl.lemmatizationOfData(tokens);

			List<String> extractedWord = Arrays.asList(tokens);
			System.out.println(extractedWord);
			/*
			 * // Extract only noun and verbs for (int i = 0; i < tokens.length; i++) {
			 * String token = tokens[i]; String nounTag = tags[i]; if
			 * (parsingServiceImpl.isNounOrVerb(nounTag)) { extractedWord.add(token); } }
			 */

			similarityServiceImpl.getSimilarityFiles(extractedWord);
			
			//matchKeywordWithMap(extractedWord);
			// searchKeywordsInFiles(compareKeyword(extractedWord));
			// return compareKeyword(extractedWord);
			return null;

		} catch (Exception e) {
			throw e;
		}
	}

	/*
	 * public void searchKeywordsInFiles(Map<String, Set<String>> matches) throws
	 * FileNotFoundException, IOException { String[] searchedContent;
	 * 
	 * for (Map.Entry<String, Set<String>> entry : matches.entrySet()) {
	 * searchedContent = readFile(entry.getKey(),entry.getValue()); } }
	 */

	public void matchKeywordWithMap(List<String> keywords) throws IOException {
		/*
		 * int max = 0; int count = 0; String fileName=""; Map<String, Set<String>>
		 * matchedKeyword = new HashMap<String, Set<String>>(); Set<String>
		 * listOfMatchedValue = new HashSet<String>(); for (Map.Entry<String,
		 * List<String>> entry : ConstantData.topWordsByFile.entrySet()) { for (String
		 * word : keywords) { Inner: for (String value : entry.getValue()) { if
		 * (word.contains(value)) { count++; listOfMatchedValue.add(value); } } if
		 * (count > max) { max = count; System.out.println("max = "+max); fileName =
		 * entry.getKey(); } }
		 * 
		 * } matchedKeyword.put(fileName, listOfMatchedValue); for (Entry<String,
		 * Set<String>> entry : matchedKeyword.entrySet()) { System.out.println("Key :"
		 * + entry.getKey() + "\n" + "Values :" + entry.getValue()); }
		 */

	}	
	/*
	 * private String[] readFile(String key, Set<String> value) throws
	 * FileNotFoundException, IOException { File file = new File(fileDirectory); try
	 * (BufferedReader reader = new BufferedReader(new FileReader(file))) { String
	 * line; while ((line = reader.readLine()) != null) { tokens =
	 * parsingServiceImpl.tokenizeData(line); for (String token : tokens) { for
	 * (String word : value) { if (word.equalsIgnoreCase(token)) {
	 * System.out.println(key + " " + word + ","); } } } } } return null; }
	 */

}
