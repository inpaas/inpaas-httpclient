package com.inpaas.http;

import java.net.InetAddress;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Objects;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inpaas.http.model.HttpClientFuture;
import com.inpaas.http.model.HttpClientInvocation;
import com.inpaas.http.model.exception.HttpClientException;
import com.inpaas.http.ssl.ExtendedSSLContextBuilder;
import com.inpaas.http.ssl.SSLHostnameVerifier;
import com.migcomponents.migbase64.Base64;

/**
 * HttpClient
 * 
 * @author jpvarandas
 */
public class HttpClient {

	protected static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

		
	protected SocketConfig getSocketConfig() {
		return SocketConfig.custom()
	        .setSoKeepAlive(false)
	        .setSoLinger(1)
	        .setSoReuseAddress(true)
	        .setSoTimeout(5000)
	        .setTcpNoDelay(true).build();
	}
	
	protected SSLConnectionSocketFactory getSSLSocketFactory(HttpClientInvocation options) throws HttpClientException {
		try {	
			Map<String, Object> ssl = options.getSsl();
			String protocol = ssl.containsKey("protocol") ? String.valueOf(ssl.get("protocol")) : "TLSv1";		
			
			
			ExtendedSSLContextBuilder ssb = new ExtendedSSLContextBuilder(protocol);
	
			if (ssl.containsKey("keystore")) {	
				KeyStore ks = (KeyStore) ssl.get("keystore");
				String secret = (String) ssl.get("secret");
				
				logger.info("getSSLSocketFactory: loadKeyMaterial({})", ks);
	
				ssb.loadKeyMaterial(ks, secret.toCharArray());
			}
			
			KeyStore truststore = null;
			if (ssl.containsKey("truststore")) { 
				truststore = (KeyStore) ssl.get("truststore");
				
				logger.info("getSSLSocketFactory: useTrustStore({})", truststore);			
			}		
			
	    	ssb.loadTrustMaterial(truststore);
	    	
	    	return new SSLConnectionSocketFactory(ssb.build(), new String[] { protocol }, null, SSLHostnameVerifier.getInstance());

		} catch (NoSuchAlgorithmException e) {
			throw HttpClientException.unwrap(e);

		} catch (Throwable e) {
			throw HttpClientException.unwrap(e);

		} finally {
			
		}
		
	}
	
	protected HttpClientConnectionManager getConnectionManager(HttpClientInvocation options, SSLConnectionSocketFactory sslsf) {
		RegistryBuilder<ConnectionSocketFactory> rb = RegistryBuilder.create();

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(rb
        		.register("http", new PlainConnectionSocketFactory())
                .register("https", sslsf)
                .build());
        
        cm.setMaxTotal(10);
        cm.setDefaultMaxPerRoute(2);
        
        return cm;
	}
	
	protected org.apache.http.client.HttpClient getHttpClient(HttpClientInvocation options) throws HttpClientException {
		
		try {
			SSLConnectionSocketFactory sslsf = getSSLSocketFactory(options);
			
			return HttpClients.custom()
					.disableAuthCaching()
					.disableAutomaticRetries()
					.disableContentCompression()
					.disableCookieManagement()
					.setConnectionReuseStrategy(new NoConnectionReuseStrategy())
					.setDefaultSocketConfig(getSocketConfig())
					.setSSLSocketFactory(sslsf)
					.setConnectionManager(getConnectionManager(options, sslsf))
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
	
	protected void proccessHeaders(HttpRequestBase xhr, Map<String, Object> headers) {
		// default headers
		xhr.addHeader("Accept", nvl(headers, "Accept", "application/json;q=0.9,text/javascript,text/xml,text/plain;q=0.8,*/*;q=0.1"));
		xhr.addHeader("User-Agent", nvl(headers, "User-Agent", "inPaaS/2.2"));		

		// handle host header
		try {
			xhr.addHeader("X-Agent-Host", Base64.encodeToString(InetAddress.getLocalHost().getAddress(), false));
			
		} catch (Exception e) {

		}
		
		// handle custom headers 
		if (headers == null) return;
		logger.info("proccessHeaders('{}')", headers);
		

		for (String key: headers.keySet()) {
			if (headers.get(key) == null) continue;
			
			xhr.addHeader(key, nvl(headers, key, ""));
		}					
	}

	protected void proccessBody(HttpEntityEnclosingRequestBase xhr, HttpClientInvocation options) {
		EntityBuilder eb = EntityBuilder.create();
		
		ContentType contentType = ContentType.parse(options.getContentType());
		eb.setContentType(contentType);

		// parse opt[data]
		Object data = options.getData();
		if (data != null) {		
			logger.info("proccessBody({}, {})", contentType, data.getClass());
			
			if (data instanceof String) {
				eb.setText((String) data);
	
			} else if (data instanceof byte[]) {
				eb.setBinary((byte[]) data);
	
			} else {
				try {
					eb.setText(new ObjectMapper().writeValueAsString(data));
					
				} catch (Throwable e) {
					eb.setText(String.valueOf(data));
					
				}
	
			}
		}

		xhr.setEntity(eb.build());
	}
		
	protected void proccessURI(HttpRequestBase xhr, String url) {
		logger.info("processURI('{}')", url);
		
		xhr.setURI(java.net.URI.create(url));
	}

	protected HttpRequestBase getRequest(HttpClientInvocation options) throws HttpClientException {
		
		HttpRequestBase xhr;
		
		switch (options.getMethod()) {
			case "DELETE":
				xhr = new HttpDelete();
	
				break;
			case "GET":
				xhr = new HttpGet();
	
				break;
			case "POST":
				HttpPost httpPost = new HttpPost();
				proccessBody(httpPost, options);
	
				xhr = httpPost;
				break;
			case "PUT":
				HttpPut httpPut = new HttpPut();
				proccessBody(httpPut, options);
	
				xhr = httpPut;
				break;
			default:
				throw new HttpClientException("error.httpclient.unsupportedmethod", null);

		}

		logger.info("getRequest(): {}", xhr.getClass().getName());
		
		return xhr;
	}

	public HttpClientFuture execute(HttpClientInvocation options) throws HttpClientException {
		Objects.requireNonNull(options.getUrl(), "error.httpclient.emptyurl");
		
		org.apache.http.client.HttpClient httpClient = null;
		HttpResponse response = null;

		try {
			HttpRequestBase xhr = getRequest(options);
			proccessURI(xhr, options.getUrl());
			proccessHeaders(xhr, options.getHeaders());

			httpClient = getHttpClient(options);
			
			options.setStarted();
			response = httpClient.execute(xhr);

			int statusCode = response.getStatusLine().getStatusCode();
			options.setResponseData(statusCode, options.getResponseProcessor().apply(response), statusCode >= 300);
			
		} catch (HttpClientException e) {
			throw e;

		} catch (Throwable e) {
			throw HttpClientException.unwrap(e);
			// e.getCause().printStackTrace();
		} finally {
			
		}
		
		return new HttpClientFuture(options);
	}
	
	public static HttpClientFuture execute(Map<String, Object> opts) throws HttpClientException {
		return new HttpClient().execute(HttpClientInvocation.fromMap(opts));		
	}
	
	public static HttpClientFuture get(String url) throws HttpClientException {
		return new HttpClient().execute(HttpClientInvocation.fromURL(url));		
	}
	
	public static HttpClientFuture post(String url, Map<String, Object> data) throws HttpClientException {
		return new HttpClient().execute(HttpClientInvocation.fromOptions("POST", url, data));				
	}
	
	public static HttpClientFuture put(String url, Map<String, Object> data) throws HttpClientException {
		return new HttpClient().execute(HttpClientInvocation.fromOptions("PUT", url, data));
	}

	public static HttpClientFuture delete(String url) throws HttpClientException {
		return new HttpClient().execute(HttpClientInvocation.fromOptions("DELETE", url, null));
	}
	
	
}
