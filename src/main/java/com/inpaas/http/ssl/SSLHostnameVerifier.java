package com.inpaas.http.ssl;

import javax.net.ssl.SSLException;

import org.apache.http.conn.ssl.AbstractVerifier;

public class SSLHostnameVerifier extends AbstractVerifier {

	private static SSLHostnameVerifier instance;
	
	public static SSLHostnameVerifier getInstance() {
		if (instance == null) instance = new SSLHostnameVerifier();
		
		return instance;
	}
	
	protected SSLHostnameVerifier() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
		

	}

}
