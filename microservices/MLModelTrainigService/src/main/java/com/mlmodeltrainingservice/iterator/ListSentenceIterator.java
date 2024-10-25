package com.mlmodeltrainingservice.iterator;

import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;

import java.util.Iterator;
import java.util.List;

public class ListSentenceIterator implements SentenceIterator {

    private final Iterator<String> sentenceIterator;
    private final List<String> sentences;

    // Constructor that takes the list of sentences
    public ListSentenceIterator(List<String> sentences) {
        this.sentences = sentences;
        this.sentenceIterator = sentences.iterator();
    }

    @Override
    public String nextSentence() {
        return sentenceIterator.next();
    }

    @Override
    public boolean hasNext() {
        return sentenceIterator.hasNext();
    }

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SentencePreProcessor getPreProcessor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPreProcessor(SentencePreProcessor preProcessor) {
		// TODO Auto-generated method stub
		
	}

    
}
