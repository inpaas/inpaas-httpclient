package com.inpaas.http.model;

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
	
	@JsonValue
	@Override
	public String toString() {
		return name().toLowerCase();
	}
	
}
