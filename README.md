# inpaas-httpclient
HTTP Client for REST and SOAP Services

# Overview

This project contains a general-purpose http-client functionality, it wraps the Apache HTTPComponents and some other libraries into a unique library for execution REST or SOAP requests.

# Get it!

## Maven

Functionality of this package is contained in Java package `com.inpaas.http`, and can be used using following Maven dependency:

```xml
<properties>
  ...
  <!-- Use the latest version whenever possible. -->
  <inpaas.httpclient.version>0.6.2</inpaas.httpclient.version>
  ...
</properties>

<dependencies>
  ...
  <dependency>
    <groupId>com.inpaas</groupId>
    <artifactId>inpaas-httpclient</artifactId>
    <version>${inpaas.httpclient.version}</version>
  </dependency>
  ...
</dependencies>
```

# Use It!

## SOAP Service
```java

	HttpClientServiceFactory
			.getImporter(ServiceType.SOAP)
			.importService("http://www.w3schools.com/xml/tempconvert.asmx")
			.getEndpoint("FahrenheitToCelsius")
			.buildInvocation(() -> {
				Map<String, Object> data = new LinkedHashMap<>();
				data.put("Fahrenheit", 80);
				
				return data;					
			}).invoke().writeTo(System.out);
		

```

# Release Notes

## Version 0.6.2 (Current)

* Apache HTTPMime package is now available to convert XML and JSON data;
* You can now override the responseProcessor for a single execution.

## Version 0.6.1
 
* Support for TLS 1.1 and 1.2 is now working properly.

## Version 0.6.0

* The header X-Agent-Host has been removed for security reasons.


