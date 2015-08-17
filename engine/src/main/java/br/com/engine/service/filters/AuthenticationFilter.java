package br.com.engine.service.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import br.com.commons.exceptions.InvalidTokenException;
import br.com.commons.transport.SessionObject;
import br.com.engine.persistence.cache.SessionCache;

/**
 * Servlet Filter implementation class AuthenticationFilterr
 */
public class AuthenticationFilter implements Filter {

	private static final Logger LOGGER = Logger.getLogger(AuthenticationFilter.class);
	
	private static final String TOKEN_PROPERTY = "token";
	
	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		// When HttpMethod comes as OPTIONS, just acknowledge that it accepts.
        if ( req.getMethod().equals( "OPTIONS" ) ) {
            LOGGER.info( "HTTP Method (OPTIONS) - Detected!" );
            // Just send a OK signal back to the browser
            return;
        }
        String uri = req.getRequestURI();
        if (uri.endsWith("login")) {
        	chain.doFilter(request, response);
        	return;
		}
        
        LOGGER.info("Authentication request for: " + req.getRequestURI());

		String token = req.getHeader(TOKEN_PROPERTY);
		if (token == null || token.isEmpty()) {
			LOGGER.error("Route: " + req.getRequestURI() + " token null or empty.");
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
		
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
	}
}
