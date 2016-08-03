package com.inpaas.http.ssl;

import java.net.Socket;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.X509ExtendedKeyManager;

public class MyX509ExtendedKeyManager extends X509ExtendedKeyManager {

	/*
	 * The default PKIX X509ExtendedTrustManager. Decisions are delegated to it,
	 * and a fall back to the logic in this class is performed if the default
	 * X509ExtendedTrustManager does not trust it.
	 */
	X509ExtendedKeyManager keyManager;

	MyX509ExtendedKeyManager(KeyStore ks, final char[] keyPassword) throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException  {
		// create a "default" JSSE X509ExtendedTrustManager.
		
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(ks, keyPassword);

		KeyManager kms[] = kmf.getKeyManagers();

		for (int i = 0; i < kms.length; i++) {
			if (kms[i] instanceof X509ExtendedKeyManager) {
				keyManager = (X509ExtendedKeyManager) kms[i];
				return;
			}
		}
	}

	@Override
	public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
		return keyManager.chooseClientAlias(keyType, issuers, socket);
	}

	@Override
	public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
		return keyManager.chooseServerAlias(keyType, issuers, socket);
	}

	@Override
	public X509Certificate[] getCertificateChain(String alias) {
		return keyManager.getCertificateChain(alias);
	}

	@Override
	public String[] getClientAliases(String keyType, Principal[] issuers) {
		return keyManager.getClientAliases(keyType, issuers);
	}

	@Override
	public PrivateKey getPrivateKey(String alias) {
		return keyManager.getPrivateKey(alias);
	}

	@Override
	public String[] getServerAliases(String keyType, Principal[] issuers) {
		return keyManager.getServerAliases(keyType, issuers);
	}

}
