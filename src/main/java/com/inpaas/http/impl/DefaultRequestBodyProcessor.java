package com.inpaas.http.impl;

import com.inpaas.http.api.RequestBodyProcessor;
import com.inpaas.http.model.HttpClientInvocation;
import com.inpaas.http.utils.JSON;

public class DefaultRequestBodyProcessor implements RequestBodyProcessor {

	public final Object apply(HttpClientInvocation hci) throws Exception {

		// parse opt[data]
		Object data = hci.getData();
		if (data == null)  return null;
		
		if (data instanceof String) 
			return data;

		if (data instanceof byte[]) 
			return data;

		return JSON.stringify(data);
				
	}

}
