package com.techforcebuddybl.services.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.CollectionSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Word2VecTraineFromJSON {

	public static void trainWordTwoVecJson() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(
				new File(System.getProperty("user.dir")+"/src/main/resources/json/output.json"));

		List<String> documents = new ArrayList<>();

		// Iterate through the JSON structure
		for (JsonNode sections : rootNode) {
			for (JsonNode section : sections) {
				String heading = section.get("heading").asText();
				String subheading = section.get("subheading").asText();
				String content = section.get("section").asText();

				// Combine heading, subheading, and content into one document
				String combinedDocument = heading + " " + subheading + " " + content;
				documents.add(combinedDocument);
			}
		}

		// Convert the documents into sentences for the iterator
		SentenceIterator sentenceIterator = new CollectionSentenceIterator(documents);

		// TokenizerFactory will split sentences into words (tokens)
		DefaultTokenizerFactory tokenizer = new DefaultTokenizerFactory();

		// Build the Word2Vec model
		Word2Vec vec = new Word2Vec.Builder()
				.minWordFrequency(3) // Words appearing less than 1 times will be ignored
				.iterations(10) // Number of iterations over the corpus
				.layerSize(100) // Size of word vectors
				.seed(42) // For reproducibility
				.negativeSample(10)
				.windowSize(2) // Context window size
				.iterate(sentenceIterator) // Use the sentence iterator
				.tokenizerFactory(tokenizer) // Use the tokenizer
				.build();

		// Train the model
		vec.fit();

		// Save the model to a file
		WordVectorSerializer.writeWord2VecModel(vec,
				new File(System.getProperty("user.dir") + "/src/main/resources/AiModal/word2vecModel.bin"));
	}

}
