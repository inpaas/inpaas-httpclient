package com.inpaas.http.soap;

import javax.wsdl.xml.WSDLLocator;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import com.inpaas.http.HttpClient;
import com.inpaas.http.model.HttpClientInvocation;
import com.inpaas.http.model.HttpService;
import com.inpaas.http.model.HttpServiceDefinition;
import com.inpaas.http.model.exception.HttpClientException;

public class WSDLLocatorImpl implements WSDLLocator {

	protected static final Logger logger = LoggerFactory.getLogger(WSDLLocatorImpl.class);

	private final HttpService service;
	
	private String latestImportURI;
	
	public WSDLLocatorImpl(HttpService service) {
		this.service = service;
	}
	
	protected HttpServiceDefinition getWSDL(String wsdlUrl) {
		String wsdlPath = wsdlUrl;
		if (wsdlUrl.startsWith(service.getBaseURL()))
			wsdlPath = wsdlUrl.substring( service.getBaseURL().length() );
		
		if (service.getDefinitions().containsKey(wsdlPath)) 
			return service.getDefinitions().get(wsdlPath);

		latestImportURI = wsdlUrl;
		
		HttpClientInvocation hci = new HttpClientInvocation();
		hci.setMethod("GET");
		hci.withResponseProcessor(response -> {
			try {
				return IOUtils.toString(response.getEntity().getContent());
			} catch (Exception e) {
				throw new HttpClientException("error.httpclient.response.io", e);
			}
		});
		
		if (wsdlUrl.startsWith("?")) 
			wsdlUrl = service.getBaseURL().concat(wsdlUrl);
		if (!wsdlUrl.startsWith("http")) 
			wsdlUrl = service.getBaseURL().substring(0, service.getBaseURL().lastIndexOf("/") + 1).concat(wsdlUrl);

		hci.setUrl(wsdlUrl);
		hci.getHeaders().put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");

		String defWsdl = hci.invoke().throwErrors().toString();
		
		HttpServiceDefinition serviceDef = new HttpServiceDefinition(wsdlPath, defWsdl);
		service.addDefinition(serviceDef);

		return serviceDef;		
	}
	
	@Override
	public InputSource getBaseInputSource() {
		logger.info("getBaseInputSource: {}", service.getBaseURL());
		
		return getWSDL("?wsdl").getInputSource();
	}

	@Override
	public InputSource getImportInputSource(String parentLocation, String importLocation) {
		logger.info("getImportInputSource: {}", importLocation);

		return getWSDL(importLocation).getInputSource();
	}

	@Override
	public String getBaseURI() {
		return service.getBaseURL();
	}

	@Override
	public String getLatestImportURI() {
		return latestImportURI;
	}

	@Override
	public void close() {
		
	}

}
