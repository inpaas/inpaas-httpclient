package com.inpaas.http.api;

import org.apache.http.HttpResponse;

import com.inpaas.http.model.exception.HttpClientException;

@FunctionalInterface
public interface ResponseProcessor {
	
	Object apply(HttpResponse response) throws HttpClientException;
	
}
