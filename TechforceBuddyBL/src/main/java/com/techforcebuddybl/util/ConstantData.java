package com.techforcebuddybl.util;

import java.util.HashMap;
import java.util.Map;

public class ConstantData {
	
	public static final Map<String, String[]> policyKeywords;
	
	static {
		policyKeywords = new HashMap<String,String[]>();
		policyKeywords.put("leave", new String[]{"hii","dates"});
		policyKeywords.put("software", new String[]{"byy","kiya"});
		policyKeywords.put("resign", new String[]{"h","kem"});
		policyKeywords.put("vacation", new String[]{"byy","dates"});
		policyKeywords.put("hr", new String[]{"byy","dates","ppp"});
	}
	
}
