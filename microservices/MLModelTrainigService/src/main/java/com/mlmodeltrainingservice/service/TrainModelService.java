package com.mlmodeltrainingservice.service;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public interface TrainModelService {
	
	public void trainModelUsingTextFileContent(List<String> sentences);
	
	public void trainModelUSingJSonFileContent(JsonNode rootNode);
	
}
