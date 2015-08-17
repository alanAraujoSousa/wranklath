package br.com.engine.service.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.commons.annotations.Authorize;
import br.com.commons.enums.PermissionEnum;
import br.com.commons.transport.UserObject;
import br.com.engine.business.controller.UserController;

@Component
@Path("/user")
public class UserRest {
	private static final Logger LOGGER = Logger.getLogger(UserRest.class);

	@Autowired
	private UserController userController;

	@POST
	@Path("/login")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.TEXT_PLAIN })
	public Response login(final UserObject user) {

		final String token = this.userController.login(user.getLogin(),
				user.getPassword());
		return Response.status(Status.OK).entity(token).build();
	}

	@POST
	@Path("/logout")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response logout(@HeaderParam("token") final String token) {
		this.userController.logout(token);
		return Response.status(Status.OK).build();
	}

	@Authorize({ PermissionEnum.DELETE_USER })
	@DELETE
	@Path("/{userLogin}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response deleteUser(@HeaderParam("token") final String token, @PathParam("userLogin") String userLoginInformed) throws Exception {
		this.userController.delete(userLoginInformed);
		return Response.status(Status.OK).build();
	}

	@Authorize({ PermissionEnum.CREATE_USER })
	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response registerUser(final UserObject user) throws Exception {
		UserObject createdUser = this.userController.create(user);
		return Response.status(Status.CREATED)
				.entity(new GenericEntity<UserObject>(createdUser) {
				}).build();
	}

	@Authorize({ PermissionEnum.LIST_ALL_USER })
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response listAllUsers(@HeaderParam("token") final String token) {
		final List<UserObject> listUserObject = this.userController
				.listAllUsers();
		if (!listUserObject.isEmpty()) {
			return Response.ok(
					new GenericEntity<List<UserObject>>(listUserObject) {
					}).build();
		} else {
			return Response.status(Status.NO_CONTENT).build();
		}
	}

	/* Test */
	@POST
	@Path("/verify")
	public Response verifyToken(@HeaderParam("token") final String token) {

		LOGGER.debug("verify");
		
		this.userController.extendTokenExpiration(token);
		return Response.status(Status.OK).build();
	}
}
