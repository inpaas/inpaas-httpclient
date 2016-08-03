package com.inpaas.http.log;

import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Logging implements Log {

	private final Logger logger;
	
	public Logging(String name) {
		logger = LoggerFactory.getLogger(name);
	}
	
	@Override
	public boolean isDebugEnabled() {
		return true;
	}

	@Override
	public boolean isErrorEnabled() {
		return true;
	}

	@Override
	public boolean isFatalEnabled() {
		return true;
	}

	@Override
	public boolean isInfoEnabled() {
		return true;
	}

	@Override
	public boolean isTraceEnabled() {
		return true;
	}

	@Override
	public boolean isWarnEnabled() {
		return true;
	}

	@Override
	public void trace(Object message) {
		logger.trace(String.valueOf(message));		
	}

	@Override
	public void trace(Object message, Throwable t) {
		logger.trace(String.valueOf(message), t);		
	}

	@Override
	public void debug(Object message) {
		logger.debug(String.valueOf(message));				
	}

	@Override
	public void debug(Object message, Throwable t) {
		logger.debug(String.valueOf(message), t);						
	}

	@Override
	public void info(Object message) {
		logger.info(String.valueOf(message));				
		
	}

	@Override
	public void info(Object message, Throwable t) {
		logger.info(String.valueOf(message), t);				
		
	}

	@Override
	public void warn(Object message) {
		logger.warn(String.valueOf(message));				

	}

	@Override
	public void warn(Object message, Throwable t) {
		logger.debug(String.valueOf(message), t);				
		
	}

	@Override
	public void error(Object message) {
		logger.error(String.valueOf(message));				

	}

	@Override
	public void error(Object message, Throwable t) {
		logger.error(String.valueOf(message), t);				
		
	}

	@Override
	public void fatal(Object message) {
		logger.error(String.valueOf(message));				
		
	}

	@Override
	public void fatal(Object message, Throwable t) {
		logger.error(String.valueOf(message), t);				
		
	}

	
}
