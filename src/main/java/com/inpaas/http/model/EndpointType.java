package com.inpaas.http.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EndpointType {

	SOAP(ServiceType.SOAP),
	
	SOAP12(ServiceType.SOAP),
	
	HTTP(ServiceType.REST);
	
	private final ServiceType serviceType;
	
	private EndpointType(ServiceType serviceType) {
		this.serviceType = serviceType;
	}
	
	public ServiceType getServiceType() {
		return serviceType;
	}
	
	@JsonCreator
	public EndpointType parse(String value) {
		for(EndpointType type: values()) {
			if (type.name().toLowerCase().equals(value)) return type;
		}
		
		return null;
	}
	
	
	@JsonValue
	@Override
	public String toString() {
		return name().toLowerCase();
	}
	
}
