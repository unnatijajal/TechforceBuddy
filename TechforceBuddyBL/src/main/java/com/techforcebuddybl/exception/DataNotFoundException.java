package com.techforcebuddybl.exception;

/*
 * This is the custom exception class. 
 * Which extends the RuntimeException
 */
public class DataNotFoundException extends RuntimeException{
	String message;

	public DataNotFoundException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return this.message;
	}
	
}
