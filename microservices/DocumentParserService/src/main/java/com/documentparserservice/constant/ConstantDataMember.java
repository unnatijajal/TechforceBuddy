package com.documentparserservice.constant;

import java.io.File;

public class ConstantDataMember {
	
		// Navigate to the TextFiles directory
		public static final File WORD_STOP_FILE = new File(System.getProperty("user.dir") + "/src/main/resources", "englishStopWords.txt");

		public static final File STANDFORD_PROPERTIES_FILE = new File(System.getProperty("user.dir") + "/src/main/resources",
				"standford-corenlp.properties");
		
		public static final String TEXT_FILE_DIR = System.getProperty("user.dir") + "/src/main/resources/TextFiles";
	
		public static final String JSON_DIR =  System.getProperty("user.dir") + "/src/main/resources/json";
		
		public static final String HOST_NAME = "http://192.168.1.214:";
}
