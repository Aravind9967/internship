package com.telehealth.screening.Exceptions;

@SuppressWarnings("serial")
public class BadRequestException extends RuntimeException {
    public BadRequestException(String msg) {
    	super(msg); 
    }
}
