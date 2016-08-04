package com.inpaas.http.soap;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inpaas.http.api.RequestBodyProcessor;
import com.inpaas.http.model.HttpClientInvocation;
import com.inpaas.http.model.HttpService;
import com.inpaas.http.model.HttpServiceEndpoint;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.WSDLParserContext;
import com.predic8.wstool.creator.RequestCreator;
import com.predic8.xml.util.ResourceResolver;

import groovy.util.IndentPrinter;
import groovy.util.Node;
import groovy.util.XmlNodePrinter;
import groovy.util.XmlParser;
import groovy.xml.MarkupBuilder;

public class SoapRequestBodyProcessor implements RequestBodyProcessor {

	private static final Logger logger = LoggerFactory.getLogger(SoapRequestBodyProcessor.class);

	private final HttpServiceEndpoint endpoint;
	
	private boolean omitEmptyAttributes = true;
	
	public SoapRequestBodyProcessor(HttpServiceEndpoint endpoint) {
		this.endpoint = endpoint;
	}
	
	public SoapRequestBodyProcessor withOmitEmptyAttributes(boolean option) {
		this.omitEmptyAttributes = option;
		
		return this;
	}
	
	@Override
	public Object apply(HttpClientInvocation hci) throws Exception {

		logger.debug("[{}] buildRequest: {}/{}", hci.getId(), hci.getService(), hci.getEndpoint());
		
		InputStream is = new ByteArrayInputStream(endpoint.getService().getBaseDefinition().getWsdl().getBytes());
		WSDLParserContext ctx = new WSDLParserContextImpl(endpoint.getService());
		ctx.setInput(is);
		
		Definitions wsdl = new WSDLParserImpl().parse(ctx);
		
		StringWriter soapBody = new StringWriter();
		MarkupBuilder mb = new MarkupBuilder(soapBody);
		mb.setOmitEmptyAttributes(omitEmptyAttributes);
		mb.setOmitNullAttributes(omitEmptyAttributes);
				
		com.predic8.wstool.creator.SOARequestCreator creator = new com.predic8.wstool.creator.SOARequestCreator(wsdl, new RequestCreator(), mb);		

		Object dataObject = hci.getData();
		
		Map<String, ?> dataMap;
		if (dataObject instanceof Map) 
			dataMap = (Map<String, ?>) dataObject;
		else
			dataMap = new ObjectMapper().convertValue(dataObject, Map.class);
		
		Map<String, String> xpathParams = buildParams(endpoint.getInputName(), dataMap, new LinkedHashMap<>());
		StringWriter sw = new StringWriter();
		xpathParams.entrySet().forEach(param -> sw.write("\n\t" + param.getKey() + ": " + param.getValue()));		
		logger.debug("buildParams: {}", sw);
		creator.setFormParams(xpathParams);
		
		creator.createRequest(endpoint.getPortType(), endpoint.getName(), endpoint.getBindingName());

		String xml = soapBody.toString();
		if (endpoint.getService().isWsaAddressing()) {
			StringWriter soapHeader = new StringWriter();
			soapHeader.write("<s12:Header xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">");
			soapHeader.write("<wsa:Action>" + endpoint.getMethod() + "</wsa:Action>");
			soapHeader.write("<wsa:To>" + endpoint.getMethod() + "</wsa:To>");
			soapHeader.write("</s12:Header>");
			
			xml = xml.replace("<s12:Body>", soapHeader.toString() + "<s12:Body>");
		}
		
		return !omitEmptyAttributes ? xml : wipeEmptyAttributes(xml);
			
	}
	
	@SuppressWarnings("unchecked")
	protected String wipeEmptyAttributes(String xml) {
		try {
			Node parser = new XmlParser().parseText( xml );
			parser.depthFirst().stream().forEach(it -> {
				Node node = (Node) it;
				if (node.children().size() == 0) 
					node.parent().remove(node);
			});
			
			StringWriter xmlsw = new StringWriter();
			XmlNodePrinter xnp = new XmlNodePrinter(new IndentPrinter(xmlsw));
			xnp.setPreserveWhitespace(true);
			xnp.print( parser );
			xml = xmlsw.toString();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return xml;
	}
	

	protected Map<String, String> buildParams(String prefix, Map<String, ?> data, Map<String, String> params) {
		if (data == null) return params;

		int i = 0;
		for (String key: data.keySet()) {
			Object o = data.get(key);
			
			String paramName;
			
			if (NumberUtils.isNumber(key)) {
				paramName = prefix + "[" + key + "]";
			} else {
				paramName = prefix + "/" + key;				
			}
			
			if (o == null) continue;
			if (o instanceof Map) {
				buildParams(paramName, (Map) o, params);
				
			} else {
				params.put("xpath:/" +  paramName, String.valueOf(o));
				
			}
			
			i++;
			
		}
		
		return params;
	}
	

	
	public static class WSDLResolver extends com.predic8.xml.util.ExternalResolver {
	
		private final HttpService service;
		
		public WSDLResolver(HttpService service) {
			this.service = service;
		}
		
		@Override
		public Object resolve(Object input, Object baseDir) {
			String uri = null;
			
			if ( input instanceof com.predic8.schema.Import) {
				uri = ((com.predic8.schema.Import) input).getSchemaLocation();
						
			} else if (input instanceof com.predic8.wsdl.Import) {
				uri = ((com.predic8.wsdl.Import) input).getLocation();
				
			} else if (input instanceof com.predic8.schema.Include) {
				uri = ((com.predic8.schema.Include) input).getSchemaLocation();
				
			} else if (input instanceof com.predic8.wadl.Include) {
				uri = ((com.predic8.wadl.Include) input).getFullPath();
				
			} else {
				uri = String.valueOf(input);
				
			}

			logger.debug("resolve({})", uri);
			
			return new WSDLLocatorImpl(service).getWSDL(uri).getInputStream();
		}
		
	};
	
	public static class WSDLParserContextImpl extends WSDLParserContext {
		
		private final HttpService service;
		
		public WSDLParserContextImpl(HttpService service) {
			this.service = service;
		}

		@Override
		public ResourceResolver getResourceResolver() {
			return new WSDLResolver(service);
		}
		
	}
	
	public static class WSDLParserImpl extends com.predic8.wsdl.WSDLParser {

	
	};

}
