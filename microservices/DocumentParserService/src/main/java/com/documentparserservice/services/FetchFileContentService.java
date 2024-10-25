package com.documentparserservice.services;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public interface FetchFileContentService {

	public List<String> getTextFileContent() throws IOException;
	
	public JsonNode getJSonFileContent() throws IOException;
}
