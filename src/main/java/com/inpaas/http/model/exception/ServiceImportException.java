package com.inpaas.http.model.exception;

public class ServiceImportException extends Exception {

	private final String url;
	
	public ServiceImportException(String message, String url, Throwable cause) {
		super(message, cause);
		
		this.url = url;		
	}
	
	public final String getUrl() {
		return url;
	}
	
}
