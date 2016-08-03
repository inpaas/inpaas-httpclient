package com.inpaas.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inpaas.http.api.HttpClientImport;
import com.inpaas.http.api.HttpClientInvocationBuilder;
import com.inpaas.http.model.ServiceType;

public interface HttpClientServiceFactory {

	public static final Logger logger = LoggerFactory.getLogger(HttpClientServiceFactory.class);

	public static HttpClientImport getImporter(ServiceType type) {
		logger.info("getImporter: {}", type);
		
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
		logger.info("getInvocationBuilder: {}", type);
		
		switch (type) {
			case SOAP:
				return new SOAPClientImpl();
			case REST:
				return null;
			default:
				throw new NullPointerException("error.httpclient.notimplemented");
		}		
		
	}

}
