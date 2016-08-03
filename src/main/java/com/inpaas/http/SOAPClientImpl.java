package com.inpaas.http;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inpaas.http.api.HttpClientImport;
import com.inpaas.http.api.HttpClientInvocationBuilder;
import com.inpaas.http.model.HttpClientInvocation;
import com.inpaas.http.model.HttpService;
import com.inpaas.http.model.exception.ServiceImportException;
import com.inpaas.http.soap.WSDLClientInvocationBuilder;
import com.inpaas.http.soap.WSDLImportService;

public class SOAPClientImpl implements HttpClientImport, HttpClientInvocationBuilder {

	protected static final Logger logger = LoggerFactory.getLogger(SOAPClientImpl.class);
	
	@Override
	public HttpService importService(String url) throws ServiceImportException {
		logger.info("importWSDL: {}", url);
		
		try {
			return WSDLImportService.getInstance().readWsdl(url);
			
		} catch (Throwable e) {
			throw new ServiceImportException("error.wsdlimport", url, e);
			
		}
	}

	@Override
	public HttpClientInvocation buildRequest(HttpService service, String operationName, Map<String, Object> data) {
		return WSDLClientInvocationBuilder.getInstance().buildRequest(service, operationName, data);
	} 
	
}
