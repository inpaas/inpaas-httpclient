package com.inpaas.http.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;

import org.xml.sax.InputSource;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class HttpServiceDefinition {

	private Integer id;
	
	private String path;
	
	private String wsdl;
	
	public HttpServiceDefinition() {

	}

	public HttpServiceDefinition(String path, String wsdl) {
		this.path = path;
		this.wsdl = wsdl;
	}
	
	@JsonIgnore
	public final Integer getId() {
		return id;
	}

	public final String getPath() {
		return path;
	}

	public final String getWsdl() {
		return wsdl;
	}

	public final void setId(Integer id) {
		this.id = id;
	}

	public final void setPath(String path) {
		this.path = path;
	}

	public final void setWsdl(String wsdl) {
		this.wsdl = wsdl;
	}
	
	@JsonIgnore
	public final InputSource getInputSource() {
		return new InputSource(new StringReader(wsdl));
	}
	
	@JsonIgnore
	public final InputStream getInputStream() {
		return new ByteArrayInputStream(wsdl.getBytes());
	}
	
	
	
	
	
}
