package com.inpaas.http;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Objects;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inpaas.http.model.HttpClientInvocation;
import com.inpaas.http.model.exception.HttpClientException;
import com.inpaas.http.ssl.ExtendedSSLContextBuilder;
import com.inpaas.http.ssl.SSLHostnameVerifier;
import com.migcomponents.migbase64.Base64;

/**
 * Main wrapper for ApacheHTTPClient
 * 
 * @author jpvarandas
 */
public class HttpClient {

	private static final String DEFAULT_ACCEPT = "application/json;q=0.9,text/javascript,text/xml,text/plain;q=0.8,*/*;q=0.1";
	private static final String DEFAULT_USERAGENT = "inpaas-httpclient/0.5";	
	private static final String DEFAULT_PROTOCOL = "TLSv1";
	
	protected static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

	protected static String localAddrs = null;
	
	protected synchronized String getLocalAddress() throws UnknownHostException {
		if (localAddrs == null) localAddrs = Base64.encodeToString(InetAddress.getLocalHost().getAddress(), false); 
		
		return localAddrs;
	}
	
	public HttpClient() {

	}

	protected SocketConfig getSocketConfig() {
		return SocketConfig.custom()
	        .setSoKeepAlive(false)
	        .setSoLinger(1)
	        .setSoReuseAddress(true)
	        .setSoTimeout(5000)
	        .setTcpNoDelay(true).build();
	}
	
	protected SSLConnectionSocketFactory getSSLSocketFactory(HttpClientInvocation hci) throws HttpClientException {
		try {	
			Map<String, Object> ssl = hci.getSsl();
			String protocol = ssl.containsKey("protocol") ? String.valueOf(ssl.get("protocol")) : DEFAULT_PROTOCOL;		
			
			
			ExtendedSSLContextBuilder ssb = new ExtendedSSLContextBuilder(protocol);
	
			if (ssl.containsKey("keystore")) {	
				KeyStore ks = (KeyStore) ssl.get("keystore");
				String secret = (String) ssl.get("secret");
				
				ssb.loadKeyMaterial(ks, secret.toCharArray());
			}
			
			KeyStore truststore = null;
			if (ssl.containsKey("truststore"))  
				truststore = (KeyStore) ssl.get("truststore");			
			
	    	ssb.loadTrustMaterial(truststore);
	    	
	    	return new SSLConnectionSocketFactory(ssb.build(), new String[] { protocol }, null, SSLHostnameVerifier.getInstance());

		} catch (NoSuchAlgorithmException e) {
			throw HttpClientException.unwrap(e);

		} catch (Throwable e) {
			throw HttpClientException.unwrap(e);

		} finally {
			
		}
		
	}
	
	protected HttpClientConnectionManager getConnectionManager(SSLConnectionSocketFactory sslsf) {
		RegistryBuilder<ConnectionSocketFactory> rb = RegistryBuilder.create();

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(rb
        		.register("http", new PlainConnectionSocketFactory())
                .register("https", sslsf)
                .build());
        
        cm.setMaxTotal(10);
        cm.setDefaultMaxPerRoute(2);
        
        return cm;
	}
	
	protected org.apache.http.client.HttpClient getHttpClient(HttpClientInvocation hci) throws HttpClientException {
		
		try {
			SSLConnectionSocketFactory sslsf = getSSLSocketFactory(hci);
			
			return HttpClients.custom()
					.disableAuthCaching()
					.disableAutomaticRetries()
					.disableContentCompression()
					.disableCookieManagement()
					.setConnectionReuseStrategy(new NoConnectionReuseStrategy())
					.setDefaultSocketConfig(getSocketConfig())
					.setSSLSocketFactory(sslsf)
					.setConnectionManager(getConnectionManager(sslsf))
					.build(); 

		} catch(Exception e) {
			throw HttpClientException.unwrap(e);
			
		}
	}
	
	@SuppressWarnings("unchecked")
	private <I> I nvl(Map<String, Object> map, String key, I nvl) {
		if (map.containsKey(key) && map.get(key) != null)
			return (I) map.get(key);
		else
			return nvl;
		
	}
	
	
	
	protected void proccessHeaders(HttpClientInvocation hci, HttpRequestBase xhr) {
		Map<String, Object> headers = hci.getHeaders();
		
		// default headers
		xhr.addHeader("Accept", nvl(headers, "Accept", DEFAULT_ACCEPT));
		xhr.addHeader("User-Agent", nvl(headers, "User-Agent", DEFAULT_USERAGENT));		

		// handle host header
		try {
			xhr.addHeader("X-Agent-Host", getLocalAddress());
			
		} catch (Exception e) {

		}
		
		// handle custom headers 
		if (headers == null) return;

		for (String key: headers.keySet()) {
			if (headers.get(key) == null) continue;
			
			xhr.addHeader(key, nvl(headers, key, ""));
		}					
	}

	protected void proccessBody(HttpClientInvocation hci, HttpEntityEnclosingRequestBase xhr)  {
		try {
			EntityBuilder eb = EntityBuilder.create();
			
			ContentType contentType = ContentType.parse(hci.getContentType());
			eb.setContentType(contentType);
	
			Object data = hci.getRequestBodyProcessor().apply(hci);
			
			if (data == null) 
				return;			
			else if (data instanceof String) 
				eb.setText((String) data);
			else if (data instanceof byte[]) 
				eb.setBinary((byte[]) data);
			else if (data instanceof InputStream)
				eb.setStream((InputStream) data);
			else if (data instanceof File) 
				eb.setFile((File) data);
			else if (data instanceof NameValuePair[])
				eb.setParameters((NameValuePair[]) data);
			else if (data instanceof Serializable)
				eb.setSerializable((Serializable) data);
				
			xhr.setEntity(eb.build());

		} catch (JsonProcessingException e) {
			throw new HttpClientException("error.httpclient.jsonwriter", e);
			
		} catch (Throwable e) {
			throw new HttpClientException("error.httpclient.body", e);

		}
		
	}
		
	protected void proccessURI(HttpClientInvocation hci, HttpRequestBase xhr) {
		xhr.setURI(java.net.URI.create(hci.getUrl()));
	}

	protected HttpRequestBase getRequest(HttpClientInvocation hci) throws HttpClientException {
		
		HttpRequestBase xhr;
		
		switch (hci.getMethod()) {
			case "DELETE":
				xhr = new HttpDelete();
	
				break;
			case "GET":
				xhr = new HttpGet();
	
				break;
			case "POST":
				HttpPost httpPost = new HttpPost();
				proccessBody(hci, httpPost);
	
				xhr = httpPost;
				break;
			case "PUT":
				HttpPut httpPut = new HttpPut();
				proccessBody(hci, httpPut);
	
				xhr = httpPut;
				break;
			case "PATCH":
				HttpPatch httpPatch = new HttpPatch();
				proccessBody(hci, httpPatch);
	
				xhr = httpPatch;
				break;
			default:
				throw new HttpClientException("error.httpclient.unsupportedmethod", null);

		}

		return xhr;
	}
	
	public void execute(HttpClientInvocation hci) {
		Objects.requireNonNull(hci.getUrl(), "error.httpclient.emptyurl");
		
		org.apache.http.client.HttpClient httpClient = null;
		HttpResponse response = null;

		try {
			if (hci.getLogging() != null) hci.getLogging().accept(hci);
			
			if (HttpClientServiceFactory.getHttpServiceProvider() != null) 
				HttpClientServiceFactory.getHttpServiceProvider().createServiceInvocation(hci);

			HttpRequestBase xhr = getRequest(hci);
			proccessURI(hci, xhr);
			proccessHeaders(hci, xhr);

			httpClient = getHttpClient(hci);
			
			
			response = httpClient.execute(xhr);

			int statusCode = response.getStatusLine().getStatusCode();
			
			if (response.getEntity() != null) {
				hci.setResponseData(statusCode, hci.getResponseProcessor().apply(response), statusCode >= 300);
				logger.info(hci.getMarker(), "execute - method: {}, url: {}, status: {}, bytes: {}, type: {}, elapsed: ", hci.getMethod(), hci.getUrl(), statusCode, response.getEntity().getContentLength(), response.getEntity().getContentType(), hci.getEndedAt() - hci.getStartedAt());
			} else {
				hci.setResponseData(statusCode, null, statusCode >= 300);
				logger.info(hci.getMarker(),"execute - method: {}, url: {}, status: {}, elapsed: ", hci.getMethod(), hci.getUrl(), statusCode, hci.getEndedAt() - hci.getStartedAt());
			}
			
		} catch (HttpClientException e) {
			logger.error(hci.getMarker(), "execute - method: {}, url: {}, error: {}", hci.getMethod(), hci.getUrl(), e.getMessage(), e);
			throw e;

		} catch (Throwable e) {
			logger.error(hci.getMarker(), "execute - method: {}, url: {}, error: {}", hci.getMethod(), hci.getUrl(), e.getMessage(), e);
			throw HttpClientException.unwrap(e);
			
		} finally {
			if (HttpClientServiceFactory.getHttpServiceProvider() != null) 
				HttpClientServiceFactory.getHttpServiceProvider().updateServiceInvocation(hci);

			if (hci.getLogging() != null) hci.getLogging().accept(hci);
			
		}
	}
	
	
}
