package com.inpaas.http.ssl;

import java.net.Socket;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedTrustManager;

public class MyX509ExtendedTrustManager extends X509ExtendedTrustManager {

	
	
	/*
	 * The default PKIX X509ExtendedTrustManager. Decisions are delegated to it,
	 * and a fall back to the logic in this class is performed if the default
	 * X509ExtendedTrustManager does not trust it.
	 */
	X509ExtendedTrustManager pkixTrustManager;

	MyX509ExtendedTrustManager(KeyStore ks) throws NoSuchAlgorithmException, KeyStoreException {
		// create a "default" JSSE X509ExtendedTrustManager.
		
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(ks);

		TrustManager tms[] = tmf.getTrustManagers();

		/*
		 * Iterate over the returned trust managers, looking for an instance of
		 * X509ExtendedTrustManager. If found, use that as the default trust
		 * manager.
		 */
		for (int i = 0; i < tms.length; i++) {
			if (tms[i] instanceof X509ExtendedTrustManager) {
				pkixTrustManager = (X509ExtendedTrustManager) tms[i];
				return;
			}
		}
	}

	/*
	 * Delegate to the default trust manager.
	 */
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		try {
			pkixTrustManager.checkClientTrusted(chain, authType);
		} catch (CertificateException excep) {
			// do any special handling here, or rethrow exception.
		}
	}

	/*
	 * Delegate to the default trust manager.
	 */
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		try {
			pkixTrustManager.checkServerTrusted(chain, authType);
		} catch (CertificateException excep) {
			/*
			 * Possibly pop up a dialog box asking whether to trust the cert
			 * chain.
			 */
		}
	}

	/*
	 * Connection-sensitive verification.
	 */
	public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket)
			throws CertificateException {
		try {
			pkixTrustManager.checkClientTrusted(chain, authType, socket);
		} catch (CertificateException excep) {
			// do any special handling here, or rethrow exception.
		}
	}

	public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
			throws CertificateException {
		try {
			pkixTrustManager.checkClientTrusted(chain, authType, engine);
		} catch (CertificateException excep) {
			// do any special handling here, or rethrow exception.
		}
	}

	public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket)
			throws CertificateException {
		try {
			pkixTrustManager.checkServerTrusted(chain, authType, socket);
		} catch (CertificateException excep) {
			// excep.printStackTrace();
			// do any special handling here, or rethrow exception.
		}
	}

	public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
			throws CertificateException {
		try {
			pkixTrustManager.checkServerTrusted(chain, authType, engine);
		} catch (CertificateException excep) {
			// do any special handling here, or rethrow exception.
		}
	}

	/*
	 * Merely pass this through.
	 */
	public X509Certificate[] getAcceptedIssuers() {
		return pkixTrustManager.getAcceptedIssuers();
	}
}
