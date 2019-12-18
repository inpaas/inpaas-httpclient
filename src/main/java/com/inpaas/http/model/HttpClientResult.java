package com.inpaas.http.model;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpClientResult {

	public int status;
	
	public String statusText;
	
	public Map<String, String> headers;
	
	public byte[] data;

	public HttpClientResult() {
		this.headers = new LinkedHashMap<>();
	}
	
	public final int getStatus() {
		return status;
	}

	public final void setStatus(int status) {
		this.status = status;
	}

	public final String getStatusText() {
		return statusText;
	}

	public final void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	public final Map<String, String> getHeaders() {
		return headers;
	}

	@JsonIgnore
	public final byte[] getData() {
		return data;
	}

	public final void setData(byte[] data) {
		this.data = data;
	}
	
	public final String getText() {
		if (data == null) return null;
		
		return new String(data, Charset.defaultCharset());
	}
	
	@JsonIgnore
	public final String getText(String charset) throws UnsupportedEncodingException {
		if (data == null) return null;

		return new String(data, charset);
	}
	
	@JsonIgnore
	public final Object getJson() throws JsonParseException, JsonMappingException, IOException {
		if (data == null) return null;
		
		final String jsondata = getText();
		
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

	
	
}
