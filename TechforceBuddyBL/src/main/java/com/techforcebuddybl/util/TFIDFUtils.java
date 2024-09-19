package com.techforcebuddybl.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;
import org.springframework.stereotype.Service;

@Service
public class TFIDFUtils {

    private Directory index = new ByteBuffersDirectory();
    private Analyzer analyzer = new EnglishAnalyzer();

    // Preprocess the text: tokenize, remove stop words, lemmatize, etc.
    public List<String> preprocessText(String content) throws IOException {
        List<String> result = new ArrayList<>();
        try (Reader stringReader = new StringReader(content);
             var tokenStream = analyzer.tokenStream(null, stringReader)) {
            CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                result.add(charTermAttribute.toString());
            }
            tokenStream.end();
        }
        return result;
    }

    // Index the documents
    public void indexDocument(String documentContent) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        try (IndexWriter writer = new IndexWriter(index, config)) {

            // Define the FieldType to store term vectors
            FieldType fieldType = new FieldType();
            fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
            fieldType.setStoreTermVectors(true);
            fieldType.setStoreTermVectorPositions(true);
            fieldType.setStoreTermVectorOffsets(true);
            fieldType.setTokenized(true);

            Document doc = new Document();
            Field contentField = new Field("content", documentContent, fieldType);
            doc.add(contentField);
            writer.addDocument(doc);
        }
    }

    // Calculate TF-IDF scores and return top N words
    public List<String> calculateTFIDF(int topN) throws Exception {
        try (IndexReader reader = DirectoryReader.open(index)) {
            ClassicSimilarity similarity = new ClassicSimilarity();

            Map<String, Float> tfidfScores = new HashMap<>();
            for (int i = 0; i < reader.maxDoc(); i++) {
                Terms terms = reader.getTermVector(i, "content");

                if (terms != null) {
                    TermsEnum termsEnum = terms.iterator();
                    BytesRef term = null;

                    while ((term = termsEnum.next()) != null) {
                        String termText = term.utf8ToString();
                        Term termInstance = new Term("content", termText);
                        long termFreq = reader.totalTermFreq(termInstance);
                        float tf = similarity.tf(termFreq);
                        float idf = similarity.idf(reader.docFreq(termInstance), reader.maxDoc());
                        float tfidf = tf * idf;
                        tfidfScores.put(termText, tfidf);
                    }
                }
            }

            // Sort the scores in descending order and return top N words
            return tfidfScores.entrySet().stream()
                    .sorted(Map.Entry.<String, Float>comparingByValue().reversed())
                    .limit(topN)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        }
    }
}
