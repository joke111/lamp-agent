package lamp.agent.genie.spring.boot.register.support.http;

import com.fasterxml.jackson.core.Base64Variants;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BasicAuthHttpRequestInterceptor implements ClientHttpRequestInterceptor {

	private final String encodedAuth;

	public BasicAuthHttpRequestInterceptor(String username, String password) {
		String auth = username + ":" + password;
		encodedAuth = "Basic " + Base64Variants.MIME_NO_LINEFEEDS.encode(auth.getBytes(StandardCharsets.US_ASCII));
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
		throws IOException {
		request.getHeaders().add("Authorization", encodedAuth);
		return execution.execute(request, body);
	}

}
