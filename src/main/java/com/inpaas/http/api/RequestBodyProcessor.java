package com.inpaas.http.api;

import com.inpaas.http.model.HttpClientInvocation;

@FunctionalInterface
public interface RequestBodyProcessor {
	
	Object apply(HttpClientInvocation hci) throws Exception;
	
}
