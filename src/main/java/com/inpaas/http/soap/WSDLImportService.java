package com.inpaas.http.soap;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.http.HTTPOperation;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.wsdl.extensions.soap12.SOAP12Operation;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inpaas.http.model.EndpointType;
import com.inpaas.http.model.HttpService;
import com.inpaas.http.model.HttpServiceEndpoint;
import com.inpaas.http.model.ServiceType;

public class WSDLImportService {

	private static final Logger logger = LoggerFactory.getLogger(WSDLImportService.class);

	private static WSDLImportService instance;
	
	public static WSDLImportService getInstance() {
		if (instance == null) instance = new WSDLImportService();
		
		return instance;
	}
	
	private WSDLImportService() {

	}
	
	public HttpService readWsdl(String url) throws WSDLException {
		
		// url cleanup
		if (url.indexOf("?") > -1) url = url.substring(0, url.indexOf("?"));			

		HttpService def = new HttpService();
		def.setType(ServiceType.SOAP);
		
		def.setScannedAt(System.currentTimeMillis());
		
		String requestURI = url.substring(url.indexOf("://") + 3);
		def.setSchema( url.substring(0, url.indexOf("://")) );		
		def.setHost( requestURI.substring(0, requestURI.indexOf("/")) );
		def.setPath( requestURI.substring(requestURI.indexOf("/")) );
		
		Definition doc = parseWSDL(def);

		String tns = doc.getTargetNamespace();
		logger.info("reading services/operations ...");		
		
		@SuppressWarnings("unchecked")
		Set<QName> keys = doc.getServices().keySet();		
		
		for(QName skey: keys) {
			
			String serviceName = skey.getLocalPart();		 
			logger.info("Service: {}", serviceName);		
			
			Service service = doc.getService(skey);

			@SuppressWarnings("unchecked")
			Set<String> ports = service.getPorts().keySet();
			for(String portName: ports) {
				Port port = doc.getService(skey).getPort(portName);
				
				@SuppressWarnings("unchecked")
				List<BindingOperation> ops = port.getBinding().getBindingOperations();
				
				for(BindingOperation op: ops) {
					def.addEndpoint(readOperation(def, service, port, op));
				}
				
			}
		}
		
		return def;		
	}		
	
	protected Definition parseWSDL(HttpService service) throws WSDLException {
		WSDLReader reader = javax.wsdl.factory.WSDLFactory.newInstance().newWSDLReader();
		reader.setFeature("javax.wsdl.verbose", false);
        reader.setFeature("javax.wsdl.importDocuments", true);
		
		return reader.readWSDL(new WSDLLocatorImpl(service));
	}
	
	protected HttpServiceEndpoint readOperation(HttpService def, Service service, Port port, BindingOperation op) {
		HttpServiceEndpoint endpoint = new HttpServiceEndpoint(def);
		endpoint.setName(op.getName());
		endpoint.setBindingName(port.getBinding().getQName().getLocalPart());
		endpoint.setPortType(port.getBinding().getPortType().getQName().getLocalPart());
		
		// Map<String, Object> input = readParts(op.getOperation().getInput().getMessage().getParts());
		
		@SuppressWarnings("unchecked")
		List<ExtensibilityElement> exs = op.getExtensibilityElements();
		exs.addAll(port.getExtensibilityElements());
		
		for(ExtensibilityElement ext: exs) {
			if (ext instanceof SOAPOperation) {
				endpoint.setMethod(((SOAPOperation) ext).getSoapActionURI());
				endpoint.setType(EndpointType.SOAP);

			} else if (ext instanceof SOAP12Operation) {
				endpoint.setMethod(((SOAP12Operation) ext).getSoapActionURI());
				endpoint.setType(EndpointType.SOAP12);
				
			} else if (ext instanceof SOAPAddress) {
				processEndpointAddress(endpoint, ((SOAPAddress) ext).getLocationURI());
				
			} else if (ext instanceof SOAP12Address) {
				processEndpointAddress(endpoint, ((SOAP12Address) ext).getLocationURI());
				
			} else if (ext instanceof HTTPOperation) {
				return null;
				
			}		
		}
		
		Part input = (Part) op.getOperation().getInput().getMessage().getOrderedParts(null).get(0);
		endpoint.setInputName(input.getElementName().getLocalPart());
		
		return endpoint;
	}
	
	protected Map<String, Object> readParts(Map<String, Part> parts) {
		Map<String, Object> map = new LinkedHashMap<>();

		for(String partName: parts.keySet()) {
			Part p = parts.get(partName);
			
			
			System.out.println(partName);
			
		}
		
		return map;
				
	}
	
	protected void processEndpointAddress(HttpServiceEndpoint endpoint, String url) {
		String requestURI = url.substring(url.indexOf("://") + 3);
		endpoint.setSchema( url.substring(0, url.indexOf("://")) );		
		endpoint.setHost( requestURI.substring(0, requestURI.indexOf("/")) );
		endpoint.setPath( requestURI.substring(requestURI.indexOf("/")) );
	}
	
}
