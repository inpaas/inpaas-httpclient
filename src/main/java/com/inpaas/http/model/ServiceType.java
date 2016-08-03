package com.inpaas.http.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ServiceType {
	@JsonProperty("S")
	SOAP, 
	
	@JsonProperty("R")
	REST;
	
	@Override
	public String toString() {
		return name().substring(0,  1);
	}
}
