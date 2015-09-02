package br.com.engine.controller.rest.filters;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import org.apache.log4j.Logger;

import br.com.commons.enums.PermissionEnum;
import br.com.engine.business.service.UserService;

@Priority(Priorities.AUTHORIZATION)
public class AuthorizationRestFilter implements ContainerRequestFilter {
	
	private static final Logger LOGGER = Logger.getLogger(AuthorizationRestFilter.class);

	private static final String TOKEN_PROPERTY = "token";
	
	private UserService userService;
	private PermissionEnum[] permissionsRequired;
	
	public AuthorizationRestFilter(UserService userService, PermissionEnum[] permissionsRequired) {
		this.userService = userService;
		this.permissionsRequired = permissionsRequired;
	}
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
		LOGGER.info("Authorization for: " + requestContext.getUriInfo().getPath());
		
		String token = requestContext.getHeaders().get(TOKEN_PROPERTY).get(0);
		userService.authorize(permissionsRequired, token);
	}
}
