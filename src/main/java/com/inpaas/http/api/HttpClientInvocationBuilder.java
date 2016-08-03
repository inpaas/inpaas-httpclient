package com.inpaas.http.api;

import java.util.Map;

import com.inpaas.http.model.HttpClientInvocation;
import com.inpaas.http.model.HttpService;

public interface HttpClientInvocationBuilder {
	
	HttpClientInvocation buildRequest(HttpService service, String operationName, Map<String, Object> data);
	
}
