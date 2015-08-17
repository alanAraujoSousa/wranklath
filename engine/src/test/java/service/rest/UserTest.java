package service.rest;

import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;

import br.com.commons.transport.UserObject;

public class UserTest extends LoginRestTest {

	@Test
	@Rollback(false)
	@Ignore
	public void listUsers() throws Exception {

		String url = "/user";

		List<UserObject> userList = clientResource.path(url).request()
				.header("token", token)
				.get(new GenericType<List<UserObject>>() {
				});

		assert (userList.size() > 0);
	}

	@Test
	@Rollback(false)
//	@Ignore
	public void deleteUser() throws Exception {

		String userLogin = "loginQualquer";
		String url = "/user/" + userLogin;

		clientResource.path(url).request().header("token", token).delete();
	}

	@Test
	@Rollback(false)
	@Ignore
	public void createUser() throws Exception {

		String url = "/user";

		UserObject userObject = new UserObject();

		userObject.setLogin("loginQualquer");
		userObject.setPassword("senhaQualquer");
		userObject.setName("NomeQualquer");

		clientResource.path(url).request().header("token", token)
				.post(Entity.entity(userObject, MediaType.APPLICATION_JSON));

	}
}
