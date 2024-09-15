package com.techforcebuddybl.services.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.techforcebuddybl.services.RawDataProcessingService;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;

@Service
public class RawDataProcessingServiceImpl implements RawDataProcessingService {

	int count=0;
	
	 // Helper method to check if the POS tag is a noun or verb
    private boolean isNounOrVerb(String posTag) {
        return posTag.startsWith("NN") || posTag.startsWith("VB");
    }
	
	@Override
	public List<String> divideSentenceIntoWords(String data) throws Exception {
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
				if(isNounOrVerb(nounTag)) {
					extractedWord.add(token);
				}
			}
			return extractedWord;
			
		} catch (Exception e) {
			throw e;
		}
	}

}
