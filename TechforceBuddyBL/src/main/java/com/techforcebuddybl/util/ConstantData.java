package com.techforcebuddybl.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConstantData {
	
	/* public static final Map<String, String[]> policyKeywords; */
	
	public static Map<String, List<String>> topWordsByFile = new HashMap<String, List<String>>();
	
	@Autowired
	private TFIDFUtils tfidfUtils;
	
	public void createKeywords(String fileName,String line) {
		
		try {
			tfidfUtils.indexDocument(String.join(" ", tfidfUtils.preprocessText(line)));
            List<String> topWords = tfidfUtils.calculateTFIDF(100);
            topWordsByFile.put(fileName, topWords);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		for(Entry<String, List<String>> entry:topWordsByFile.entrySet()) {
			System.out.println("Key : " + entry.getKey()+"\n"+"values: "+entry.getValue());
		}
	}
	
	/*static {
		policyKeywords = new HashMap<String,String[]>();
		
		policyKeywords.put("leave", 
				new String[]{"applicable","annual","compensatory","floating","work from home","without pay",
						"maternity","paternity","work hour","after resignation","planned",
						"un planned","medical","cancelation"});
		
		policyKeywords.put("salary", 
				new String[]{"salary account","employee salary account","account opening process",
						"required documents","contact details","bank charges","date range",
						"adjustment","tds","lop","final settlement"});
		
		policyKeywords.put("stipend", new String[]{"applicable","mode","date range","calculation"});
		
	}*/
	
}