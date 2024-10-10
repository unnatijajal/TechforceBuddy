package com.techforcebuddybl.services.impl;

import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.techforcebuddybl.services.CreateJsonOfPdfService;

@Service
public class CreateJsonOfPdfServiceImpl implements CreateJsonOfPdfService {

	// Get the current directory
    private static String currentDir = System.getProperty("user.dir");
    
    private static String currentHeading = "";
    private static String currentSubheading = "";
    private static StringBuilder sectionContent = new StringBuilder();
    private static boolean subHeadingFlag = false;
    private static JSONArray fileArray;
    private static  JSONObject jsonResult = new JSONObject();
	
	
    public static void setHeadingAndContent(String line, int scenario) {
		
    	if (scenario == 1) {
			if ( line.matches("^\\d+\\.\\s.*")) {
				// Set the current heading, removing thenumbering
				currentHeading = line.replaceAll("^\\d+\\.\\s+", ""); // Remove number and space
			} else if (line.matches("^\\d+\\.\\d+\\s+.*")) {
				if (subHeadingFlag) {
					JSONObject entry = new JSONObject();
					entry.put("heading", currentHeading);
					entry.put("subheading", currentSubheading.replaceFirst("^\\d+\\.\\d+\\s+", "")); // Add the subheading
					entry.put("section", sectionContent.toString().trim());
					fileArray.put(entry);
					sectionContent.setLength(0);
					subHeadingFlag = false;
				}
				currentSubheading = line;
				subHeadingFlag = true;
			} else {
				sectionContent = sectionContent.append(line + "\n");
			}
		} else if (scenario == 2) {
			if ( line.matches("^\\d+\\.\\s.*")) {
				if (subHeadingFlag) {
					JSONObject entry = new JSONObject();
					entry.put("heading", currentHeading);
					entry.put("subheading", currentSubheading); // Add the subheading
					entry.put("section", sectionContent.toString().trim());
					fileArray.put(entry);
					sectionContent.setLength(0);
					subHeadingFlag = false;
				}
				// Set the current heading, removing the numbering
				currentHeading = line.replaceAll("^\\d+\\.\\s+", ""); // Remove number and space
				subHeadingFlag = true;

			} else if (line.matches("^\\d+\\.\\d+\\s+.*") || line.startsWith("")) {
				sectionContent = sectionContent.append(line.replaceFirst("^\\d+\\.\\d+\\s+", "") + "\n");
			}
		} else if (scenario == 3) {
			if ( line.matches("^\\d+\\.\\s.*")) {
				if (subHeadingFlag) {
					JSONObject entry = new JSONObject();
					entry.put("heading", currentHeading);
					entry.put("subheading", currentSubheading); // Add the subheading
					entry.put("section", sectionContent.toString().trim());
					fileArray.put(entry);
					sectionContent.setLength(0);
					subHeadingFlag = false;
				}
				// Set the current heading, removing the numbering
				currentHeading = line.replaceAll("^\\d+\\.\\s+", ""); // Remove number and space
				subHeadingFlag = true;

			} else if (line.matches("^[•*\\-o]\\s.*") || line.startsWith("")) {
				sectionContent = sectionContent.append(line.replaceFirst("^[•*\\-o]\\s", "") + "\n");
			}
		}
    }
    
    
	@Override
	public void createJsonOfPdf(String[] lines, String fileName) throws IOException {
		fileArray = new JSONArray();
        for (String line : lines) {
        	
            line = line.trim(); // Trim leading and trailing spaces
            if(line.equalsIgnoreCase("") || line.equalsIgnoreCase("\r")) {
            	continue;
            }
            if(fileName.contains("IT"))
				setHeadingAndContent(line, 1);
			else if(fileName.contains("Compensatory") || fileName.contains("WFH")
					|| fileName.equalsIgnoreCase("ti_leave_policy_2024.pdf")) {
				setHeadingAndContent(line, 2);
			}else if(fileName.contains("Payroll") || fileName.contains("Sexual") 
					|| fileName.contains("Parental") || fileName.contains("BYOD")) {
				setHeadingAndContent(line, 3);
			}
            
        }
        if(subHeadingFlag) {
			JSONObject entry = new JSONObject();
            entry.put("heading", currentHeading);
            entry.put("subheading", currentSubheading); // Add the subheading
            entry.put("section", sectionContent.toString().trim());
            fileArray.put(entry);
            sectionContent.setLength(0);
            subHeadingFlag = false;
            currentSubheading="";
		}
        jsonResult.put(fileName, fileArray);
      
	}

    public void saveJSONToFile() throws IOException {
        try (FileWriter file = new FileWriter(currentDir + "/src/main/resources/json/output.json")) {
            file.write(jsonResult.toString(4)); // Indent for readability
            file.flush();
        }
    }
	
}
