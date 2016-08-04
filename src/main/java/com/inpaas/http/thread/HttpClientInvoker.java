package com.inpaas.http.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.inpaas.http.HttpClient;
import com.inpaas.http.model.HttpClientFuture;
import com.inpaas.http.model.HttpClientInvocation;

public class HttpClientInvoker {

	private static ThreadGroup httpThreadGroup;
	
	private static ThreadPoolExecutor executor;
	
	private static final int MAX_THREADS = 50;
	
	private static final String THREAD_SUFFIX = "HttpClientThread";
	
	
	public static ThreadGroup getHttpThreadGroup() {
		if (httpThreadGroup == null) httpThreadGroup = new ThreadGroup("httpclient");
		return httpThreadGroup;
	}
	
	public static ExecutorService getExecutor() {
		if (executor == null) {
			executor = new ThreadPoolExecutor(MAX_THREADS, MAX_THREADS,
		        5, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new HttpClientThreadFactory()
                ); 
		
			executor.allowCoreThreadTimeOut(true);
		}
		
		//executor.
		return executor;
	}
	
	private static class HttpClientThreadFactory implements ThreadFactory {
		@Override
		public Thread newThread(Runnable r) {			
			Thread httpThread = new Thread(getHttpThreadGroup(), r);			
			httpThread.setName(THREAD_SUFFIX + "-" + httpThread.getId());
			httpThread.setDaemon(false);						
			
			return httpThread;
		}		
	}
	
	public static HttpClientFuture invoke(HttpClientInvocation hci) {
		return new HttpClientFuture(hci, CompletableFuture.supplyAsync(() -> {
			new HttpClient().execute(hci);
			
			return hci;
		}, getExecutor()), hci.getTimeout());
		
	}
	
	
}
