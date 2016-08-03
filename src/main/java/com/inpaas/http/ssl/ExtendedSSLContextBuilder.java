package com.inpaas.http.ssl;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

public class ExtendedSSLContextBuilder {

	private final String protocol;
	private final Set<KeyManager> keyManagers;
	private final Set<TrustManager> trustManagers;
	private final SecureRandom secureRandom = new SecureRandom();

	public ExtendedSSLContextBuilder(String protocol) {
		this.protocol = protocol;
		this.keyManagers = new LinkedHashSet<>();
		this.trustManagers = new LinkedHashSet<>();
	}

	public ExtendedSSLContextBuilder loadKeyMaterial(final KeyStore keystore, final char[] keyPassword) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
		this.keyManagers.add(new MyX509ExtendedKeyManager(keystore, keyPassword));
		return this;
	}

	public ExtendedSSLContextBuilder loadTrustMaterial(KeyStore truststore)
			throws NoSuchAlgorithmException, KeyStoreException {
		this.trustManagers.add(new MyX509ExtendedTrustManager(truststore));

		return this;
	}

	public SSLContext build() throws NoSuchAlgorithmException, KeyManagementException {
		final SSLContext sslcontext = SSLContext.getInstance(this.protocol != null ? this.protocol : "TLS");
		sslcontext.init(
				!keyManagers.isEmpty() ? keyManagers.toArray(new KeyManager[keyManagers.size()]) : null,
				!trustManagers.isEmpty() ? trustManagers.toArray(new TrustManager[trustManagers.size()]) : null,
				secureRandom);

		return sslcontext;
	}

}
