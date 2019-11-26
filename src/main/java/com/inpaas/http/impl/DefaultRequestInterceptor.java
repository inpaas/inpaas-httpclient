package com.inpaas.http.impl;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

public class DefaultRequestInterceptor implements HttpRequestInterceptor {

	private static final DefaultRequestInterceptor _instance = new DefaultRequestInterceptor();
	
	public static final DefaultRequestInterceptor getInstance() {
		return _instance;
	}
	
	private DefaultRequestInterceptor() {
		// TODO Auto-generated constructor stub
	}
	
	public final void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {		
		if (!request.containsHeader("Accept-Encoding")) {
			request.addHeader("Accept-Encoding", "gzip");
		}
	}

}
