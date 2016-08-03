package com.inpaas.http.model;

import java.io.PrintStream;

import com.fasterxml.jackson.annotation.JsonValue;
import com.inpaas.http.model.exception.HttpRequestException;
import com.inpaas.http.utils.JSON;

public class HttpClientFuture {

	private final HttpClientInvocation request;

	public HttpClientFuture(HttpClientInvocation request) {
		this.request = request;
	}

	public HttpClientInvocation getRequest() {
		return request;
	}

	public HttpClientFuture success(HttpClientCallback callback) {
		if (!getRequest().isError()) callback.accept(request);
		
		return this;
	}

	public HttpClientFuture error(HttpClientCallback callback) {
		if (getRequest().isError()) callback.accept(request);

		return this;	
	}
	
	public HttpClientFuture complete(HttpClientCallback callback) {
		callback.accept(request);
		
		return this;
	}
	
	public HttpClientFuture throwErrors() {
		if (getRequest().isError()) throw new HttpRequestException();
		
		return this;
	} 
	
	@JsonValue
	public Object getResponse() {
		return getRequest().getResponse();		
	}
	
	public HttpClientFuture writeTo(PrintStream out) {
		JSON.stringify(getResponse(), out);

		return this;
	}

	
	@Override
	public String toString() {
		return String.valueOf(getResponse());
	}
	
	@FunctionalInterface
	public static interface HttpClientCallback {
		
		void accept(Object data, HttpClientInvocation invocation) ;
		
		default void accept(HttpClientInvocation invocation) {
			accept(invocation.getResponse(), invocation);
		}	
	}

}
