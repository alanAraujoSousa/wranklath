package br.com.engine.controller.rest;

import java.util.Deque;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.commons.transport.UserObject;
import br.com.engine.business.service.UnitService;
import br.com.engine.business.service.UserService;

@Component
@Path("/unit")
public class UnitRest {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(UnitRest.class);

	@Autowired
	private UnitService unitServiceService;
	
	@Autowired
	private UserService userServiceService;

	@POST
	@Path("/{id}/move")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.TEXT_PLAIN })
	public Response move(@PathParam("id") Long id,
			@HeaderParam("token") String token, Deque<Integer> places) {
		
		UserObject user = this.userServiceService.findUserBySessionToken(token);
		this.unitServiceService.move(user, id, places);
		
		return Response.ok().build();
	}
}
