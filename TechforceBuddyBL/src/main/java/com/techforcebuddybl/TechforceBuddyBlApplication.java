package com.techforcebuddybl;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.techforcebuddybl.services.impl.ExtractDataFromPdfServiceImpl;

@SpringBootApplication
public class TechforceBuddyBlApplication {

	public static void main(String[] args) {
		SpringApplication.run(TechforceBuddyBlApplication.class, args);
		/*
		 * try { //ExtractDataFromPdfServiceImpl.accessFiles(); } catch (IOException e)
		 * { // TODO Auto-generated catch block e.printStackTrace(); }
		 */
	}

}
