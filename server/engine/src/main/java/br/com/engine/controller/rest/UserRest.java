package br.com.engine.controller.rest;

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
import br.com.commons.transport.BuildingObject;
import br.com.commons.transport.UnitObject;
import br.com.commons.transport.UserObject;
import br.com.engine.business.service.BuildingService;
import br.com.engine.business.service.UnitService;
import br.com.engine.business.service.UserService;

@Component
@Path("/user")
public class UserRest {
	private static final Logger LOGGER = Logger.getLogger(UserRest.class);

	@Autowired
	private UserService userService;
	
	@Autowired
	private UnitService unitService;
	
	@Autowired
	private BuildingService buildingService;

	@POST
	@Path("/login")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.TEXT_PLAIN })
	public Response login(final UserObject user) {

		final String token = this.userService.login(user.getLogin(),
				user.getPassword());
		return Response.ok().entity(token).build();
	}

	@POST
	@Path("/logout")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response logout(@HeaderParam("token") final String token) {
		this.userService.logout(token);
		return Response.ok().build();
	}

	@Authorize({ PermissionEnum.DELETE_USER })
	@DELETE
	@Path("/{userLogin}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response deleteUser(@HeaderParam("token") final String token,
			@PathParam("userLogin") String userLoginInformed) throws Exception {
		this.userService.delete(userLoginInformed);
		return Response.ok().build();
	}

	@Authorize({ PermissionEnum.CREATE_USER })
	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response registerUser(final UserObject user) throws Exception {
		UserObject createdUser = this.userService.create(user);
		return Response.status(Status.CREATED)
				.entity(new GenericEntity<UserObject>(createdUser) {
				}).build();
	}

	@Authorize({ PermissionEnum.LIST_ALL_USER })
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response listAllUsers(@HeaderParam("token") final String token) {
		final List<UserObject> listUserObject = this.userService.listAllUsers();
		if (!listUserObject.isEmpty()) {
			return Response.ok(
					new GenericEntity<List<UserObject>>(listUserObject) {
					}).build();
		} else {
			return Response.noContent().build();
		}
	}
	
	@GET
	@Path("/allvisible")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response listAllVisible(@HeaderParam("token") String token) {
		UserObject userObject = this.userService.findUserBySessionToken(token);
		List<Object> unitsAndBuildings = this.userService.listAllVisible(userObject);
		
		if (!unitsAndBuildings.isEmpty()) {
			return Response.ok(
					new GenericEntity<List<Object>>(unitsAndBuildings) {
					}).build();
		} else {
			return Response.noContent().build();
		}
	}
	
	@GET
	@Path("/unit")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response listAllUnits(@HeaderParam("token") final String token) {
		
		UserObject userObject = this.userService.findUserBySessionToken(token);
		final List<UnitObject> listUserObject = this.unitService.listByUser(userObject);
		if (!listUserObject.isEmpty()) {
			return Response.ok(
					new GenericEntity<List<UnitObject>>(listUserObject) {
					}).build();
		} else {
			return Response.noContent().build();
		}
	}
	
	@GET
	@Path("/building")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response listAllBuildings(@HeaderParam("token") final String token) {

		UserObject userObject = this.userService.findUserBySessionToken(token);
		final List<BuildingObject> listBuildingObjects = this.buildingService.listByUser(userObject);
		if (!listBuildingObjects.isEmpty()) {
			return Response.ok(
					new GenericEntity<List<BuildingObject>>(listBuildingObjects) {
					}).build();
		} else {
			return Response.noContent().build();
		}
	}

	@POST
	@Path("/verify")
	public Response verifyToken(@HeaderParam("token") final String token) {

		this.userService.extendTokenExpiration(token);
		return Response.ok().build();
	}

	/* Test */
	@POST
	@Path("/ping")
	public Response ping() {

		LOGGER.debug("ping");

		return Response.ok().build();
	}
}
