package com.inpaas.http.api;

import com.inpaas.http.model.HttpService;
import com.inpaas.http.model.exception.ServiceImportException;

public interface HttpClientImport {

	public HttpService importService(String url) throws ServiceImportException;
	
}
