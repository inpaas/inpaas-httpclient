package com.inpaas.http.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class SSLHostnameVerifier implements HostnameVerifier {

	private static SSLHostnameVerifier instance;
	
	public static SSLHostnameVerifier getInstance() {
		if (instance == null) instance = new SSLHostnameVerifier();
		
		return instance;
	}
	
	private SSLHostnameVerifier() {
		// TODO Auto-generated constructor stub
	}
	

	@Override
	public boolean verify(String hostname, SSLSession session) {
		return true;
	}

}
