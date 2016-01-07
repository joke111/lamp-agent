package lamp.agent.genie.spring.boot.management.controller;

import lamp.agent.genie.core.runtime.process.AppProcessType;
import lamp.agent.genie.spring.boot.LampAgent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;

import static org.fest.assertions.api.Assertions.assertThat;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(value = LampAgent.class)
@WebIntegrationTest("server.port:0")
public class AppControllerTest {

	@Autowired
	EmbeddedWebApplicationContext server;

	@Value("${local.server.port}")
	int port;

	private Authentication authentication;

	RestTemplate template = new TestRestTemplate();

	private String getBaseUrl() {
		return "http://localhost:" + port;
	}


	@Before
	public void setUp() throws Exception {
		AuthenticationManager authenticationManager = this.server
				.getBean(AuthenticationManager.class);
		this.authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken("user", "password"));

	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testRegister_PreInstalled() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(this.authentication);

		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();

		parts.add("id", "test-1");
		parts.add("name", "test");
		parts.add("type", "type");
		parts.add("version", "0.0.1");
		parts.add("processType", AppProcessType.DAEMON.name());
		parts.add("homeDirectoryPath", "/Users/kangwoo/Applications/zookeeper-3.4.7");
		parts.add("workDirectoryPath", "/Users/kangwoo/Applications/zookeeper-3.4.7");
		parts.add("pidFilePath", "/tmp/zookeeper/zookeeper_server.pid");
		parts.add("startCommandLine", "./bin/zkServer.sh start");
		parts.add("stopCommandLine", "./bin/zkServer.sh stop");
		parts.add("preInstalled", true);


		template.postForEntity(getBaseUrl() + "/api/app", parts, Void.class);
	}

	@Test
	public void testRegister_withInstall() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(this.authentication);

		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();

		parts.add("id", "test-2");
		parts.add("name", "test");
		parts.add("type", "type");
		parts.add("version", "0.0.1");
		parts.add("processType", AppProcessType.DEFAULT.name());
		parts.add("pidFilePath", "/tmp/zookeeper/zookeeper_server.pid");
		parts.add("startCommandLine", "./bin/zkServer.sh start");
		parts.add("stopCommandLine", "./bin/zkServer.sh stop");
		parts.add("preInstalled", false);
		parts.add("filename", "helloworld");
		parts.add("installFile", new FileSystemResource("/Users/kangwoo/lamp-client/genie-spring-boot/src/test/java/lamp/agent/genie/spring/boot/management/repository/AppManifestRepositoryTest.java"));
		// UrlResource

		template.postForEntity(getBaseUrl() + "/api/app", parts, Void.class);
	}

	@Test
	public void testDeregister() throws Exception {

	}
}