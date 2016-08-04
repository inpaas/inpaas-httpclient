package com.inpaas.http;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inpaas.http.api.HttpClientImport;
import com.inpaas.http.api.HttpClientInvocationBuilder;
import com.inpaas.http.api.HttpServiceProvider;
import com.inpaas.http.impl.SOAPClientImpl;
import com.inpaas.http.model.HttpClientFuture;
import com.inpaas.http.model.HttpClientInvocation;
import com.inpaas.http.model.ServiceType;
import com.inpaas.http.model.exception.HttpClientException;

public class HttpClientServiceFactory {

	public static final Logger logger = LoggerFactory.getLogger(HttpClientServiceFactory.class);
	
	private static HttpServiceProvider httpServiceProvider;

	public static HttpClientImport getImporter(ServiceType type) {
		logger.debug("getImporter: {}", type);
		
		switch (type) {
			case SOAP:
				return new SOAPClientImpl();
			case REST:
				return null;
			default:
				throw new NullPointerException("error.httpclient.notimplemented");
		}

	}

	public static HttpClientInvocationBuilder getInvocationBuilder(ServiceType type) {
		logger.debug("getInvocationBuilder: {}", type);
		
		switch (type) {
			case SOAP:
				return new SOAPClientImpl();
			case REST:
				return null;
			default:
				throw new NullPointerException("error.httpclient.notimplemented");
		}				
	}
	
	public static HttpServiceProvider getHttpServiceProvider() {
		return httpServiceProvider;
	}
	
	public static void registerServiceProvider(HttpServiceProvider serviceProviderInstance) {
		httpServiceProvider = serviceProviderInstance;
	}

	public static HttpClientFuture execute(Map<String, Object> opts) throws HttpClientException {
		return HttpClientInvocation.fromMap(opts).invoke();		
	}
	
	public static HttpClientFuture get(String url) throws HttpClientException {
		return HttpClientInvocation.fromURL(url).invoke();		
	}
	
	public static HttpClientFuture post(String url, Map<String, Object> data) throws HttpClientException {
		return HttpClientInvocation.fromOptions("POST", url, data).invoke();				
	}
	
	public static HttpClientFuture put(String url, Map<String, Object> data) throws HttpClientException {
		return HttpClientInvocation.fromOptions("PUT", url, data).invoke();
	}

	public static HttpClientFuture delete(String url) throws HttpClientException {
		return HttpClientInvocation.fromOptions("DELETE", url, null).invoke();
	}
		
	
}
