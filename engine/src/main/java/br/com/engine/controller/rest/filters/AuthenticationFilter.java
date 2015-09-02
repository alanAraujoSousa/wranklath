package br.com.engine.controller.rest.filters;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import br.com.commons.exceptions.InvalidTokenException;
import br.com.commons.transport.SessionObject;
import br.com.engine.persistence.cache.SessionCache;

@Component
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	private static final Logger LOGGER = Logger.getLogger(AuthenticationFilter.class);
	
	private static final String TOKEN_PROPERTY = "token";
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		// When HttpMethod comes as OPTIONS, just acknowledge that it accepts.
        if ( requestContext.getMethod().equalsIgnoreCase( "OPTIONS" ) ) {
            LOGGER.info( "HTTP Method (OPTIONS) - Detected!" );
            return;
        }
        
        String uri = requestContext.getUriInfo().getPath();
        if (uri.endsWith("login")) {
        	return;
		}
        
        LOGGER.info("Authentication request for: " + uri);

		String token = requestContext.getHeaders().get(TOKEN_PROPERTY).get(0);;
		if (token == null || token.isEmpty()) {
			LOGGER.error("Route: " + uri + " token null or empty.");
			throw new InvalidTokenException("Token null or empty.");
		}
		
		/*
		 * TODO
		 * Implement get session by token and ip that created it.
		 */
		SessionObject session = SessionCache.getInstance().findSessionByToken(token);
		if (session == null) {
			throw new InvalidTokenException("Token don't have a valid session.");
		}
	}
}
