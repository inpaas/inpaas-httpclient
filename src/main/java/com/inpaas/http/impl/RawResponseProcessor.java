package com.inpaas.http.impl;

import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import com.inpaas.http.api.ResponseProcessor;
import com.inpaas.http.model.HttpClientResult;
import com.inpaas.http.model.exception.HttpClientException;

public class RawResponseProcessor implements ResponseProcessor {

	private static final ResponseProcessor instance = new RawResponseProcessor();
	
	private RawResponseProcessor() {

	}
	
	public static ResponseProcessor getInstance() {
		return instance;
	}
	
	
	private java.io.InputStream getContentStream(final HttpEntity responseEntity) throws IOException {

		final Header contentEncoding = responseEntity.getContentEncoding();
		final boolean gzip = contentEncoding != null && "gzip".equalsIgnoreCase(contentEncoding.getValue());

		if (gzip) {
			return new GZIPInputStream(responseEntity.getContent());
		} else {
			return responseEntity.getContent();
		}
		
	}
		
	public Object apply(HttpResponse response) throws HttpClientException {

		HttpClientResult result = new HttpClientResult();
		result.setStatus(response.getStatusLine().getStatusCode());
		result.setStatusText(response.getStatusLine().getReasonPhrase());
		
		Header[] headers = response.getAllHeaders();
		for(int i = 0; i < headers.length; i++) {			
			result.getHeaders().put(headers[i].getName(), headers[i].getValue());
		}
		
		final HttpEntity responseEntity = response.getEntity();
		if (responseEntity != null) {
			try (final java.io.InputStream contentStream = getContentStream(responseEntity)) {
				result.setData(IOUtils.toByteArray(contentStream));
	
			} catch (Throwable e) {
				throw HttpClientException.unwrap(e);
	
			} finally {
	
			}			
		}
		
		return result;

	}

}
