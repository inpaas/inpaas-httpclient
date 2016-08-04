package com.inpaas.http.impl;

import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.inpaas.http.model.exception.HttpClientException;

public class DefaultResponseProcessor {

	private static final Logger logger = LoggerFactory.getLogger(DefaultResponseProcessor.class);
	
	public static Object proccessResponse(HttpResponse response) throws HttpClientException {
		
		try {
			int statusCode = response.getStatusLine().getStatusCode();
			String statusText = response.getStatusLine().getReasonPhrase();
			Header contentType = response.getEntity().getContentType();
			
			logger.debug("proccessResponse: {} {} '{}' with {} bytes", statusCode, statusText, contentType.getValue(), response.getEntity().getContentLength());
			
			String contentTypeText = contentType == null ? "application/json" : contentType.getValue();
			Object data = null;
			if (contentTypeText.indexOf("json") > -1 || contentTypeText.indexOf("javascript") > -1) {
				data = new ObjectMapper().readValue(response.getEntity().getContent(), Map.class);
	
			} else if (contentTypeText.indexOf("text/xml") == 0) {
				data = new XmlMapper().readValue(response.getEntity().getContent(), Map.class);
				//data = SoapEnvelopeReader.read(xml).getBody();

			}  else {
				data = IOUtils.toString(response.getEntity().getContent());
	
			}
			
			logger.debug("proccessResponse: {}\n{}", data.getClass().getName(), data);
			
			return data;
			
		} catch(Throwable e) {
			throw HttpClientException.unwrap(e);
			
		}		
		
	}

}
