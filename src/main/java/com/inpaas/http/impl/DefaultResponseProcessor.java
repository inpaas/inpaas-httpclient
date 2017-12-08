package com.inpaas.http.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.inpaas.http.api.ResponseProcessor;
import com.inpaas.http.model.exception.HttpClientException;

public class DefaultResponseProcessor implements ResponseProcessor {

	public Object apply(HttpResponse response) throws HttpClientException {
		
		try {
			if (response.getEntity() == null || response.getEntity().getContent() == null) return null;
			
			Header contentType = response.getEntity().getContentType();
			
			String contentTypeText = contentType == null ? "application/json" : contentType.getValue();
			Object data = null;
			if (contentTypeText.indexOf("json") > -1 || contentTypeText.indexOf("javascript") > -1) {
				String jsondata = IOUtils.toString(response.getEntity().getContent());
				
				if (jsondata == null || jsondata.length() == 0) 
					data = null;
				
				else if (jsondata.startsWith("["))
					data = new ObjectMapper().readValue(jsondata, List.class);
				
				else if (jsondata.startsWith("{"))
					data = new ObjectMapper().readValue(jsondata, Map.class);
				
				else
					data = new ObjectMapper().readValue(jsondata, Object.class);
	
			} else if (contentTypeText.indexOf("text/xml") == 0) {
				data = new XmlMapper().readValue(response.getEntity().getContent(), Map.class);

			}  else {
				data = IOUtils.toString(response.getEntity().getContent());
	
			}
			
			return data;
			
		} catch(Throwable e) {
			throw HttpClientException.unwrap(e);
			
		}		
		
	}

}
