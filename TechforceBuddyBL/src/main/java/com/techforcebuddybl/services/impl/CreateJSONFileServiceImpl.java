package com.techforcebuddybl.services.impl;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.techforcebuddybl.services.CreateJSONFileService;

@Service
public class CreateJSONFileServiceImpl implements CreateJSONFileService {

	// Directory path for JSON folder
	public static String  jsonFilePath = System.getProperty("user.dir")+"/src/main/resources/JSON/";
	
	@Override
	public void createJSONFileOFPDF(String text,String fileName) throws IOException {
		// Split the text into lines
        String[] lines = text.split("\n");

        // Create a JSON object
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonNode = mapper.createObjectNode();

        // Initialize the current heading and subheading
        String currentHeading = "";
        String currentSubheading = "";
        String content="";

        // Ignore header lines (assuming 2 lines)
        int headerLines = 2;
        int footerLines = 2; // Ignore footer lines (assuming 2 lines)

        // Iterate through the lines and extract headings and subheadings
        for (int i = headerLines; i < lines.length - footerLines; i++) {
            String line = lines[i].trim();
            if(line.equalsIgnoreCase("")) {
            	continue;
            }
            // Extract headings (e.g., 1, 2, 3, ...)
            else if (line.matches("\\b\\d+\\.\\s+.*")) {
                currentHeading = line.replaceAll("\\b\\d+\\.\\s*", "");
                ObjectNode headingNode = jsonNode.putObject(currentHeading);
                currentSubheading = ""; // Reset subheading
            }
         // Extract subheadings (e.g., 1.1. Subheading)
            else if (line.matches("\\b\\d+\\.\\d+\\s+.*")) {
                currentSubheading =  line.replaceFirst("\\b\\d+\\.\\d+\\s*", "");
                ObjectNode subheadingNode = ((ObjectNode) jsonNode.get(currentHeading)).putObject(currentSubheading);
            } else {
                // Add the content to the current subheading or heading
                if (currentSubheading.isEmpty()) {
                    // No subheading, add content to heading
                	content += line + "\n";
                    if (jsonNode.has(currentHeading)) {
                        ((ObjectNode) jsonNode.get(currentHeading)).put("content", line);
                    }
                } else {
                    // Add content to subheading
                    if (jsonNode.get(currentHeading).has(currentSubheading)) {
                        ((ObjectNode) jsonNode.get(currentHeading).get(currentSubheading)).put("content", line);
                    }
                }
            }
        }


        // Write the JSON object to a file
        mapper.writeValue(
        		new File(jsonFilePath + fileName.substring(0, fileName.lastIndexOf(".")) + ".json"),
        		jsonNode);
    
        System.out.println("PDF converted to JSON successfully!");
	}
	
	
}
