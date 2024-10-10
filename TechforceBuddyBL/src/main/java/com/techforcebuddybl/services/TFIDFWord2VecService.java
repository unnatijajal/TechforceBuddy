package com.techforcebuddybl.services;

import java.io.File;
import java.io.IOException;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techforcebuddybl.util.TFIDFUtils;

@Service
public class TFIDFWord2VecService {
	// Assign the location of text files to the variable.
	private String modalFileDirectory = System.getProperty("user.dir") + "/src/main/resources/AiModal";

	@Autowired
	private TFIDFUtils tfidfUtils;

	public double[] computeTFIDFWeightedWord2Vec(String documentName, String[] tokens) throws IOException {
		@SuppressWarnings("deprecation")
		Word2Vec word2VecModel = WordVectorSerializer.readWord2Vec(new File(modalFileDirectory + "/word2vecModel.txt"));

		double[] documentVector = new double[word2VecModel.getLayerSize()];
		int tokenCount = 0;

		for (String token : tokens) {
			if (word2VecModel.hasWord(token)) {
				double[] wordVector = word2VecModel.getWordVector(token);
				if (wordVector != null) {
					// Get the TF and IDF values for the token
					double tf = tfidfUtils.getTF(documentName, token);
					double idf = TFIDFUtils.idfMap.getOrDefault(token, 0.0);
					double tfidfScore = tf * idf;

					// Multiply Word2Vec vector by TF-IDF score
					for (int i = 0; i < wordVector.length; i++) {
						documentVector[i] += wordVector[i] * tfidfScore;
					}
					tokenCount++;
				}
			}
		}

		// Normalize the document vector
		if (tokenCount > 0) {
			for (int i = 0; i < documentVector.length; i++) {
				documentVector[i] /= tokenCount;
			}
		}

		return documentVector;
	}

}
