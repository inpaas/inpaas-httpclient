package com.inpaas.http.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EndpointType {

	SOAP,
	
	SOAP12,
	
	HTTP;
	
	@JsonValue
	@Override
	public String toString() {
		return name().toLowerCase();
	}
	
}
