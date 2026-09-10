package com.telehealth.screening.Exceptions;

@SuppressWarnings("serial")
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String msg) {
    	super(msg);
    	
    }
}

