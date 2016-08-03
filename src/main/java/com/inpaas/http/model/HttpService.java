package com.inpaas.http.model;

import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(Include.NON_NULL)
public class HttpService {

	private Integer id;	
	private String key;
	
	private Integer module;
	private String moduleKey;
	
	private ServiceType type;
	private String name;
	private String text;
	
	private String schema;
	private String host;
	private String path;
	
	private long scannedAt;
	
	private boolean wsaAddressing;
	
	private Map<String, HttpServiceDefinition> definitions;
	
	private Map<String, HttpServiceEndpoint> endpoints;
	
	@JsonIgnore
	private Long keyStoreFileId;

	@JsonIgnore
	private byte[] keyStoreFileData;
	
	@JsonIgnore
	private String keyStorePassword;

	public final Integer getId() {
		return id;
	}

	public final String getKey() {
		return key;
	}

	public final Integer getModule() {
		return module;
	}

	public final String getModuleKey() {
		return moduleKey;
	}

	public final String getName() {
		return name;
	}

	public final ServiceType getType() {
		return type;
	}
	
	public final String getText() {
		return text;
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
	
	public long getScannedAt() {
		return scannedAt;
	}
	
	public final String getBaseURL() {
		return schema + "://" + host + path;
	}

	public final boolean isWsaAddressing() {
		return wsaAddressing;
	}

	public final Long getKeyStoreFileId() {
		return keyStoreFileId;
	}

	public final byte[] getKeyStoreFileData() {
		return keyStoreFileData;
	}

	public final String getKeyStorePassword() {
		return keyStorePassword;
	}

	public final void setId(Integer id) {
		this.id = id;
	}

	public final void setKey(String key) {
		this.key = key;
	}

	public final void setModule(Integer module) {
		this.module = module;
	}

	public final void setModuleKey(String moduleKey) {
		this.moduleKey = moduleKey;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final void setText(String text) {
		this.text = text;
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

	public final void setWsaAddressing(boolean wsaAddressing) {
		this.wsaAddressing = wsaAddressing;
	}

	public final void setKeyStoreFileId(Long keyStoreFileId) {
		this.keyStoreFileId = keyStoreFileId;
	}

	public final void setKeyStoreFileData(byte[] keyStoreFileData) {
		this.keyStoreFileData = keyStoreFileData;
	}

	public final void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}
	
	public final void setType(ServiceType type) {
		this.type = type;
	}
	
	public void setScannedAt(long scannedAt) {
		this.scannedAt = scannedAt;
	}

	public final Map<String, HttpServiceDefinition> getDefinitions() {
		if (definitions == null) definitions = new LinkedHashMap<>();
		
		return definitions;
	}
	
	public final HttpServiceDefinition getBaseDefinition() {
		return getDefinitions().get("?wsdl");
	}
	
	public Map<String, HttpServiceEndpoint> getEndpoints() {
		if (endpoints == null) endpoints = new LinkedHashMap<>();
		
		return endpoints;
	}

	public final void addDefinition(HttpServiceDefinition definitions) {
		getDefinitions().put(definitions.getPath(), definitions);
	}

	public final void addEndpoint(HttpServiceEndpoint endpoint) {
		if (endpoint == null) return;
		
		if (getEndpoints().containsKey(endpoint.getName())) {
			HttpServiceEndpoint currentEndpoint = getEndpoints().get(endpoint.getName());
			
			if (currentEndpoint.getType() == EndpointType.SOAP12) return;
			
		}
		
		getEndpoints().put(endpoint.getName(), endpoint);
	}
	
	public final HttpServiceEndpoint getEndpoint(String endpointName) {
		return getEndpoints().get(endpointName);
	}

	public HttpService writeJSON(PrintStream out) {
		try {
			out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this));
		} catch(Exception e) {
			e.printStackTrace(out);
		}
		
		return this;
	}

	
	
	
}
