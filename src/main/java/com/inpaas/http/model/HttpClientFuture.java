package com.inpaas.http.model;

import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.inpaas.http.model.exception.HttpRequestException;
import com.inpaas.http.utils.JSON;

public class HttpClientFuture {

	private final int timeout;
	
	@JsonIgnore
	private final CompletableFuture<HttpClientInvocation> future;
	
	public HttpClientFuture(HttpClientInvocation request, CompletableFuture<HttpClientInvocation> future, int timeout) {
		this.future = future;
		
		this.timeout = timeout;
	}

	public HttpClientFuture success(HttpClientCallback callback) {
		future.thenAccept(hci -> {
			if (!hci.isError()) callback.accept(hci.getResponse(), hci);
		});
		
		return this;
	}

	public HttpClientFuture error(HttpClientCallback callback) {
		future.thenAccept(hci -> {
			if (hci.isError()) callback.accept(hci.getResponse(), hci);
		});

		return this;	
	}
	
	public HttpClientFuture complete(HttpClientCallback callback) {
		future.thenAccept(hci -> {
			callback.accept(hci.getResponse(), hci);
		});
		
		return this;
	}
	
	public HttpClientFuture throwErrors() {
		future.thenAccept(hci -> {
			if (hci.isError()) throw new HttpRequestException();			
		});
		
		return this;
	} 
	
	protected HttpClientInvocation completed() {
		try {
			return future.get(timeout, TimeUnit.SECONDS);
			
		} catch (Exception e) {
			throw new IllegalStateException(e);
			
		}		
	}
	
	@JsonValue
	public Object response() {
		try {
			return completed().getResponse();
			
		} catch (Exception e) {
			throw new IllegalStateException(e);
			
		}
	}
	
	public HttpClientFuture writeTo(PrintStream out) {
		future.thenAccept(o -> JSON.stringify(o, out));
		
		return this;
	}
	
	public HttpClientFuture andThen(Consumer<HttpClientInvocation> fn) {
		future.thenAccept(fn);
		
		return this;
	}
	
	@Override
	public String toString() {
		return String.valueOf(completed());
	}
	
	public String stringify() {
		return JSON.stringify(completed());
	}
	
	@FunctionalInterface
	public static interface HttpClientCallback {
		
		void accept(Object data, HttpClientInvocation invocation) ;
		
	}

}
