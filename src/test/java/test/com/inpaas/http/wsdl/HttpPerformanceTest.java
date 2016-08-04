package test.com.inpaas.http.wsdl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inpaas.http.HttpClientServiceFactory;

public class HttpPerformanceTest {

	public void performanceTest() throws Exception {
		int requestThreshold = 5;
		
		long startedAt = System.currentTimeMillis();
		
		for (int i = 0; i < requestThreshold; i++) {
			String json = new ObjectMapper().writeValueAsString( HttpClientServiceFactory.get("https://studio.inpaas.com/status").response() );
			
			
			
			System.out.println(json);
		}
		
		
	}
	
	public static void main(String[] args) throws Exception {
		new HttpPerformanceTest().performanceTest();
	}
	
}
