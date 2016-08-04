package com.inpaas.http.utils;

import java.io.InputStream;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.inpaas.http.model.exception.HttpClientException;
import com.inpaas.http.soap.UntypedXmlDeserializer;

public class XML {

	private static XmlMapper mapper;
	
	public static final XmlMapper getMapper() {
		if (mapper == null) {
			mapper = new XmlMapper();
			
			SimpleModule mod = new SimpleModule()
				.addDeserializer(Object.class, new UntypedXmlDeserializer());
			mapper.registerModule(mod);			
		}
		
		return mapper;
	}
	
	
	@SuppressWarnings("unchecked")
	public static final Object parse(InputStream is) {
		try {
			return getMapper().readValue(is, Object.class);
			
		} catch (Exception e) {
			throw new HttpClientException("error.httpclient.xml.parse", e);
			
		}
	}
	
}
