package com.inpaas.http.model;

import java.util.Map;
import java.util.function.Supplier;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.inpaas.http.HttpClientServiceFactory;

@JsonInclude(Include.NON_NULL)
public class HttpServiceEndpoint {

	private HttpService service;
	
	private Integer id;	
	
	private String name;
	private EndpointType type;
	
	@JsonInclude(Include.NON_NULL)
	private String schema;
	
	@JsonInclude(Include.NON_NULL)
	private String host;
	
	@JsonInclude(Include.NON_NULL)
	private String path;
	
	private String method;

	@JsonInclude(Include.NON_NULL)
	private String portType;	
	
	@JsonInclude(Include.NON_NULL)
	private String bindingName;

	@JsonInclude(Include.NON_NULL)
	private String inputName; 	
	
	private boolean rest;
	
	public HttpServiceEndpoint() {
		this.service = null;
	}
	
	public HttpServiceEndpoint(HttpService service) {
		this.service = service;
	}

	public final Integer getId() {
		return id;
	}
	
	@JsonIgnore
	public HttpService getService() {
		return service;
	}
	
	public void setService(HttpService service) {
		this.service = service;
	}
	
	public final String getName() {
		return name;
	}
	
	public final EndpointType getType() {
		return type;
	}

	public final String getPortType() {
		return portType;
	}

	public final String getBindingName() {
		return bindingName;
	}
	
	public final String getInputName() {
		return inputName;
	}

	public final String getSchema() {
		return schema;
	}

	public final String getHost() {
		return host;
	}

	public final String getPath() {
		return path;
	}

	public final String getMethod() {
		return method;
	}

	@JsonInclude(value=Include.NON_DEFAULT)
	public final boolean isRest() {
		return rest;
	}

	public final void setId(Integer id) {
		this.id = id;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final void setPortType(String portType) {
		this.portType = portType;
	}

	public final void setBindingName(String bindingName) {
		this.bindingName = bindingName;
	}
	
	public final void setInputName(String inputName) {
		this.inputName = inputName;
	}

	public final void setSchema(String schema) {
		this.schema = schema;
	}

	public final void setHost(String host) {
		this.host = host;
	}

	public final void setPath(String path) {
		this.path = path;
	}

	public final void setMethod(String method) {
		this.method = method;
	}

	public final void setRest(boolean rest) {
		this.rest = rest;
	}
	
	public final void setType(EndpointType type) {
		this.type = type;
	}
	
	@JsonIgnore
	public final String getEndpointURL() {
		return schema + "://" + host + path;
	}

	public final HttpClientInvocation buildInvocation(Map<String, Object> params) {
		return HttpClientServiceFactory.getInvocationBuilder(service.getType()).buildRequest(service, getName(), params);		
	}

	public final HttpClientInvocation buildInvocation(Supplier<Map<String, Object>> params) {
		return HttpClientServiceFactory.getInvocationBuilder(service.getType()).buildRequest(service, getName(), params.get());		
	}

	
	
	
}
