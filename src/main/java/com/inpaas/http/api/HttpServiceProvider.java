package com.inpaas.http.api;

import com.inpaas.http.model.HttpClientInvocation;
import com.inpaas.http.model.HttpService;

public interface HttpServiceProvider {

	HttpService getServiceById(Integer id);
	
	HttpService getServiceByKey(String key);
	
	void setServiceDefinition(HttpService httpService);
	
	void createServiceInvocation(HttpClientInvocation invocation);

	void updateServiceInvocation(HttpClientInvocation invocation);

}
