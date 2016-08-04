package com.inpaas.http.soap;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inpaas.http.model.HttpClientInvocation;
import com.inpaas.http.model.HttpService;
import com.inpaas.http.model.HttpServiceEndpoint;


public class WSDLClientInvocationBuilder {
	
	private static final Logger logger = LoggerFactory.getLogger(WSDLClientInvocationBuilder.class);

	private static WSDLClientInvocationBuilder instance;
	
	public static WSDLClientInvocationBuilder getInstance() {
		if (instance == null) instance = new WSDLClientInvocationBuilder();
		
		return instance;
	}
	
	private WSDLClientInvocationBuilder() {

	}
	
	public HttpClientInvocation buildRequest(HttpService service, String operationName, Map<String, Object> data) {
		HttpServiceEndpoint endpoint = service.getEndpoints().get(operationName);
		
		String url = service.getBaseURL();
		if (endpoint.getPath() != null) url = endpoint.getEndpointURL();

		HttpClientInvocation xhr = new HttpClientInvocation();
		xhr.setService(service.getKey());
		xhr.setEndpoint(operationName);
		
		xhr.setUrl(url);
		xhr.setMethod("POST");
		xhr.setContentType("application/soap+xml;charset=utf-8;action=\"" + endpoint.getMethod() + "\"");
		
		xhr.setData(data);
		xhr.withResponseProcessor(SoapClientResponseProcessor::proccessResponse);
		xhr.withRequestBodyProcessor(new SoapRequestBodyProcessor(endpoint));
			
		return xhr;

	}
}
