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
  <inpaas.osl.version>0.4.11</inpaas.osl.version>
  ...
</properties>

<dependencies>
  ...
  <dependency>
    <groupId>com.inpaas</groupId>
    <artifactId>inpaas-httpclient</artifactId>
    <version>${inpaas.osl.version}</version>
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
