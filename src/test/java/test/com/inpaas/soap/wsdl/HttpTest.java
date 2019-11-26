package test.com.inpaas.soap.wsdl;

import java.util.LinkedHashMap;
import java.util.Map;

import com.inpaas.http.model.HttpClientInvocation;

public class HttpTest {
	public static void main(String... args) {
		final Map<String, Object> json_data = new LinkedHashMap<>();
		json_data.put("client_id", "8f719fe8-773b-4e08-afaf-530d3d12b78e");
		json_data.put("client_secret", "e0d9f505bd8e47f69ea8d9398a9f015dv");
		json_data.put("code", "1a39c69a81b58bc9293269ab66955c0b");
		
		Object result = HttpClientInvocation.fromOptions("POST", "https://api.rd.services/auth/token", json_data).invoke().response();
		
		System.out.println(result);
		
	}
}
