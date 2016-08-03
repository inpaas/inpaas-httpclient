package com.inpaas.http.model.exception;

public class HttpClientException extends RuntimeException {

	
	public HttpClientException(String message, Throwable cause) {
		super(message, cause);
	}	
	
	public static final HttpClientException unwrap(Throwable cause) {
		
		if (cause == null) {
			return new HttpClientException("error.httpclient", cause);
		
		} else if (cause instanceof HttpClientException) {
			return (HttpClientException) cause;
			
		} else {
			return new HttpClientException(cause.getMessage(), cause);
			
		}
		
	}

}
