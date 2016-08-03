package com.inpaas.http.soap;

import java.util.Map;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.xml.XmlMapper;
import com.inpaas.http.model.exception.HttpClientException;

public class SoapClientResponseProcessor {

	private static final Logger logger = LoggerFactory.getLogger(SoapClientResponseProcessor.class);
	
	public static Object proccessResponse(HttpResponse response) throws HttpClientException {
		
		try {
			int statusCode = response.getStatusLine().getStatusCode();			
			logger.info("proccessResponse: {} with {} bytes", statusCode, response.getEntity().getContentLength());
			
			return new XmlMapper().readValue(response.getEntity().getContent(), Map.class);
			
		} catch(Throwable e) {
			throw HttpClientException.unwrap(e);
			
		}		
		
	}


}
