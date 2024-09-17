package com.techforcebuddybl.services.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;

import org.springframework.stereotype.Service;

import com.techforcebuddybl.services.DataParsingService;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;

@Service
public class DataParsingServiceImpl implements DataParsingService {

	@Override
	public String[] tokenizeData(String text) throws IOException {
		WhitespaceTokenizer tokenizer = WhitespaceTokenizer.INSTANCE;
		String[] tokens = tokenizer.tokenize(text);
		return tokens;
	}

	@Override
	public String[] parsingData(String[] tokens) throws IOException {
		String currentDir = System.getProperty("user.dir");
		File resourceDir = new File(currentDir + "/src/main/resources/OpenNlp_models");
		FileInputStream inputStream = new FileInputStream(new File(resourceDir,"en-pos-maxent.bin"));
		//FileInputStream inputStream = new FileInputStream("C:\\OpenNLP_models\\resourceDir");
		POSModel model = new POSModel(inputStream);
		POSTaggerME tagger = new POSTaggerME(model);
		return tagger.tag(tokens);
	}

	@Override
	public boolean isNounOrVerb(String posTag) {
		return posTag.startsWith("NN") || posTag.startsWith("VB") || posTag.startsWith("JJ");
	}
	
	

}
