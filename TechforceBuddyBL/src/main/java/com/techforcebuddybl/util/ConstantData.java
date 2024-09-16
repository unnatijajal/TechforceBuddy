package com.techforcebuddybl.util;

import java.util.HashMap;
import java.util.Map;

public class ConstantData {
	
	public static final Map<String, String[]> policyKeywords;
	
	static {
		policyKeywords = new HashMap<String,String[]>();
		
		policyKeywords.put("leave", 
				new String[]{"applicable","annual","compensatory","floating","work from home","without pay",
						"maternity","paternity","work hour","after resignation","planned",
						"un planned","medical","cancelation"});
		
		policyKeywords.put("payroll", 
				new String[]{"salary account","employee salary account","account opening process",
						"required documents","contact details","bank charges","date range",
						"adjustment","tds","lop","final settlement"});
		
		policyKeywords.put("stipend", new String[]{"applicable","mode","date range","calculation"});
		
	}
	
}