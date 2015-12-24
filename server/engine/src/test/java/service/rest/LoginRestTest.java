package service.rest;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;

import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;

import br.com.commons.transport.UserObject;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class LoginRestTest {

	protected static WebTarget clientResource;
	protected static String token;

	@Before
	@Test
	@Rollback(false)
	public void setup() throws Exception {
		String host = "127.0.0.1";
		String port = "8080";
		String projectName = "project";
		String rest = "rest";

		String uri = "http://" + host + ":" + port + "/" + projectName + "/"
				+ rest + "/";

		// FIXME Use the same mapper on code, don't create other for test.
		ResteasyJackson2Provider resteasyJacksonProvider = new ResteasyJackson2Provider();
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		resteasyJacksonProvider.setMapper(mapper);
		// FIXME Use the same mapper on code, don't create other for test.

		clientResource = ClientBuilder.newClient()
				.register(resteasyJacksonProvider).target(uri);

		token = this.login();
		assert (!token.isEmpty());
	}

	private String login() throws JsonProcessingException {

		UserObject user = new UserObject();
		user.setLogin("root");
		user.setPassword("root");

		String token = clientResource.path("/user/login").request()
				.post(Entity.json(user), String.class);

		return token;
	}
}
