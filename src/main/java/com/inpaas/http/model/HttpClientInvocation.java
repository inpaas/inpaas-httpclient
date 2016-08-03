package com.inpaas.http.model;

import java.io.ByteArrayInputStream;
import java.io.PrintStream;
import java.security.KeyStore;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inpaas.http.HttpClient;
import com.inpaas.http.HttpClientResponseProcessor;
import com.inpaas.http.api.ResponseProcessor;
import com.inpaas.http.model.exception.HttpClientException;
import com.inpaas.http.utils.JSON;

public class HttpClientInvocation {
	
	private static final String DEFAULT_METHOD = "GET";
	private static final String DEFAULT_CONTENT_TYPE = "application/json; charset=utf-8";

	private String service;
	
	private String endpoint;
	
	private String url;
	
	private String method;
	
	private String contentType;
	
	private Map<String, Object> headers;
	
	private Object data;
	
	@JsonProperty(access = Access.WRITE_ONLY)
	private Map<String, Object> ssl;
	
	private ResponseProcessor responseProcessor = HttpClientResponseProcessor::proccessResponse;
	
	@JsonProperty(access = Access.READ_ONLY)
	private int statusCode;
	
	@JsonProperty(access = Access.READ_ONLY)
	private Object response;
	
	@JsonProperty(access = Access.READ_ONLY)
	private boolean error;
	
	@JsonProperty(access = Access.READ_ONLY)
	private long startedAt;
	
	@JsonProperty(access = Access.READ_ONLY)
	private long endedAt;

	
	public void setResponseData(int statusCode, Object response, boolean error) {
		this.statusCode = statusCode;
		this.response = response;
		this.endedAt = System.currentTimeMillis();
		this.error = error;
	}
	
	public void setStarted() {
		this.startedAt = System.currentTimeMillis();
	}

	public static HttpClientInvocation fromURL(String url) {
		HttpClientInvocation i = new HttpClientInvocation();
		i.setUrl(url);
		
		return i;
	}
	
	public static HttpClientInvocation fromOptions(String method, String url, Map<String, Object> data) {
		HttpClientInvocation i = new HttpClientInvocation();
		i.setMethod(method);
		i.setUrl(url);
		i.setData(data);
		
		return i;
	}
	
	public static HttpClientInvocation fromMap(Map<String, Object> opts) {
		return new ObjectMapper().convertValue(opts, HttpClientInvocation.class);
	}
	
	

	public final String getService() {
		return service;
	}

	public final String getEndpoint() {
		return endpoint;
	}

	public final String getUrl() {
		return url;
	}

	public final String getMethod() {
		if (method == null) method = DEFAULT_METHOD;
		
		return method;
	}

	public final String getContentType() {
		if (contentType == null) contentType = DEFAULT_CONTENT_TYPE;
		return contentType;
	}

	public final Map<String, Object> getHeaders() {
		if (headers == null) headers = new LinkedHashMap<>();
		
		return headers;
	}

	public final Object getData() {
		return data;
	}

	public final Map<String, Object> getSsl() {
		if (ssl == null) ssl = new LinkedHashMap<>();
		return ssl;
	}

	public final int getStatusCode() {
		return statusCode;
	}

	public final Object getResponse() {
		return response;
	}
	
	public final boolean isError() {
		return error;
	}

	public final long getStartedAt() {
		return startedAt;
	}

	public final long getEndedAt() {
		return endedAt;
	}

	@JsonIgnore
	public ResponseProcessor getResponseProcessor() {
		return responseProcessor;
	}
	
	public final void setService(String service) {
		this.service = service;
	}

	public final void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public final void setUrl(String url) {
		this.url = url;
	}

	public final void setMethod(String method) {
		this.method = method;
	}

	public final void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public final void setHeaders(Map<String, Object> headers) {
		this.headers = headers;
	}

	public final void setData(Object data) {
		this.data = data;
	}

	public final void setSsl(Map<String, Object> ssl) {
		this.ssl = ssl;
	}

	public final HttpClientInvocation withContentType(String contentType) {
		setContentType(contentType);

		return this;
	}

	public final HttpClientInvocation withHeader(String name, String value) {
		getHeaders().put(name, value);

		return this;
	}

	public final HttpClientInvocation withData(Map<String, Object> data) {
		setData(data);

		return this;
	}
	
	public final HttpClientInvocation withResponseProcessor(ResponseProcessor responseProcessor) {
		this.responseProcessor = responseProcessor;
		
		return this;
	}
	
	public final HttpClientInvocation withSSLAuthentication(byte[] pfx, byte[] keystore, String secret) {
		try {
			
			KeyStore ts = KeyStore.getInstance("PKCS12");
			ts.load(new ByteArrayInputStream(pfx), secret.toCharArray());
			
			KeyStore jks = KeyStore.getInstance("JKS");
			jks.load(new ByteArrayInputStream(keystore), secret.toCharArray());	
			
			/*
			var CertificateFactory = Java.type("java.security.cert.CertificateFactory");
	
			var cf = CertificateFactory.getInstance("X.509");
			var cert = cf.generateCertificate(cer.getInputStream());
			
			var pfxalias = pfx.aliases().nextElement();
			var pfxKey = pfx.getKey(pfxalias, secret.toCharArray());
			var pfxchain = pfx.getCertificateChain(pfxalias);
	
			jks.setKeyEntry(pfxalias, pfxKey, secret.toCharArray(), pfxchain);
			jks.setCertificateEntry(alias, cert);
			*/
			
			// getSsl().put("truststore", ts);
			getSsl().put("keystore", jks);
			getSsl().put("secret", secret);
			
		} catch(Exception e) {
			if (e.getCause() != null && e.getCause() instanceof java.security.UnrecoverableKeyException)
				throw new HttpClientException("error.httpclient.sslcontext.password", e);
			
			throw new HttpClientException("error.httpclient.sslcontext", e);
		}

		
		return this;
	}

	public final HttpClientFuture invoke() {
		return new HttpClient().execute(this);				
	}
	

	public HttpClientInvocation writeTo(PrintStream out) {
		JSON.stringify(this, out);

		return this;
	}

	
}
