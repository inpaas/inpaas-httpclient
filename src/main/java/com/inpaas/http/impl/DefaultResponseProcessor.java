package com.inpaas.http.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.inpaas.http.api.ResponseProcessor;
import com.inpaas.http.model.exception.HttpClientException;

public class DefaultResponseProcessor implements ResponseProcessor {

	private java.io.InputStream getContentStream(final HttpEntity responseEntity) throws IOException {

		final Header contentEncoding = responseEntity.getContentEncoding();
		final boolean gzip = contentEncoding != null && "gzip".equalsIgnoreCase(contentEncoding.getValue());

		if (gzip) {
			return new GZIPInputStream(responseEntity.getContent());
		} else {
			return responseEntity.getContent();
		}
		
	}
	
	protected Object readJson(final java.io.InputStream contentStream) throws IOException {
		
		String jsondata = IOUtils.toString(contentStream);
		
		if (jsondata == null || jsondata.length() == 0) {
			return null;

		} else if (jsondata.startsWith("[")) {
			return new ObjectMapper().readValue(jsondata, List.class);

		} else if (jsondata.startsWith("{")) {
			return new ObjectMapper().readValue(jsondata, Map.class);

		} else {
			return new ObjectMapper().readValue(jsondata, Object.class);
		}
		
	}
	
	protected Object readXml(final java.io.InputStream contentStream) throws IOException {
		return new XmlMapper().readValue(contentStream, Map.class);
	}	
	
	protected Object readDefault(final java.io.InputStream contentStream) throws IOException {
		return IOUtils.toString(contentStream);
	}	

	
	public Object apply(HttpResponse response) throws HttpClientException {

		final HttpEntity responseEntity = response.getEntity();
		if (responseEntity == null) return null;
		
		try (final java.io.InputStream contentStream = getContentStream(responseEntity)) {
			
			final Header contentType = responseEntity.getContentType();
			final String contentTypeText = contentType == null ? "application/json" : contentType.getValue();

			if (contentTypeText.indexOf("json") > -1 || contentTypeText.indexOf("javascript") > -1) {
				return readJson(contentStream);

			} else if (contentTypeText.indexOf("text/xml") == 0) {
				return readXml(contentStream);

			} else {
				return readDefault(contentStream);

			}

		} catch (Throwable e) {
			throw HttpClientException.unwrap(e);

		} finally {

		}

	}

}
