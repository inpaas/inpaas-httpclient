package com.inpaas.http.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.http.conn.ssl.TrustStrategy;

public class SSLTrustStrategy implements TrustStrategy {

	private static SSLTrustStrategy instance;
	
	public static SSLTrustStrategy getInstance() {
		if (instance == null) instance = new SSLTrustStrategy();
		
		return instance;
	}
	
	protected SSLTrustStrategy() {

	}
	
	@Override
	public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		return true;
	}

}
