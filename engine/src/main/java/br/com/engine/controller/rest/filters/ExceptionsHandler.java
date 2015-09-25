package br.com.engine.controller.rest.filters;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.stereotype.Component;

import br.com.commons.exceptions.ProjectException;

import com.fasterxml.jackson.databind.JsonMappingException;

@Component
@Provider
public class ExceptionsHandler implements ExceptionMapper<Throwable> {

	@Override
	public Response toResponse(Throwable exception) {
		/**********************FIXME********************************* 
		 * printStackTrace, don't use for production. instead it 
		 * use the Logger.
		 * **********************************************************/
		exception.printStackTrace();

		if (exception instanceof WebApplicationException) {
			return ((WebApplicationException) exception).getResponse();
		}

		if (exception instanceof ProjectException) {
			Integer statusCode = ((ProjectException) exception).getStatusCode();
			return Response.status(Status.fromStatusCode(statusCode))
					.type(MediaType.APPLICATION_JSON).build();
		}

		if (exception instanceof JsonMappingException) {
			return Response.status(Status.BAD_REQUEST)
					.type(MediaType.APPLICATION_JSON).build();
		}

		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.type(MediaType.APPLICATION_JSON).build();
	}
}
