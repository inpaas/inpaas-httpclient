package com.inpaas.http.soap;

import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.inpaas.http.model.exception.HttpClientException;
import com.inpaas.http.utils.XML;

public class SoapClientResponseProcessor {

	private static final Logger logger = LoggerFactory.getLogger(SoapClientResponseProcessor.class);
	
	public static Object proccessResponse(HttpResponse response) throws HttpClientException {
		
		try {
			int statusCode = response.getStatusLine().getStatusCode();			
			logger.info("proccessResponse: {} with {} bytes", statusCode, response.getEntity().getContentLength());
			
			return XML.parse(response.getEntity().getContent());
		} catch(Throwable e) {
			throw HttpClientException.unwrap(e);
			
		}		
		
	}


}
