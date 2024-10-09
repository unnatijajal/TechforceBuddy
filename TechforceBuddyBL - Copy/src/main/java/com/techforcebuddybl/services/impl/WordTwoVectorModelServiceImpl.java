package com.techforcebuddybl.services.impl;

import java.io.File;
import java.io.FileNotFoundException;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.FileSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.springframework.stereotype.Service;

import com.techforcebuddybl.services.WordTwoVectorModelService;

/*
 * This is implementation class of WordTwoVectorModelService
 * which is has method to train modal using raw data.
 */

@Service
public class WordTwoVectorModelServiceImpl implements WordTwoVectorModelService {

	public void trainModel() throws FileNotFoundException {
		
		// Directory where your training text files are located
        File inputDir = new File(System.getProperty("user.dir")+"/src/main/resources/TextFiles");

        // SentenceIterator will go through the text file and extract sentences
        SentenceIterator iterator = new FileSentenceIterator(inputDir);

        // TokenizerFactory will split sentences into words (tokens)
        TokenizerFactory tokenizer = new DefaultTokenizerFactory();

        // Build the Word2Vec model
        Word2Vec vec = new Word2Vec.Builder()
                .minWordFrequency(1) // Words appearing less than 1 times will be ignored
                .iterations(10)       // Number of iterations over the corpus
                .layerSize(100)       // Size of word vectors
                .seed(42)             // For reproducibility
                .windowSize(2)        // Context window size
                .iterate(iterator)        // Use the sentence iterator
                .tokenizerFactory(tokenizer) // Use the tokenizer
                .build();

        // Train the model
        vec.fit();

        // Save the model to a file
        WordVectorSerializer.writeWord2VecModel(vec, new File(System.getProperty("user.dir")+"/src/main/resources/AiModal/word2vecModel.txt"));

	}
	
}
