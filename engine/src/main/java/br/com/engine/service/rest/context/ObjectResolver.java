package br.com.engine.service.rest.context;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Component
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class ObjectResolver implements ContextResolver<ObjectMapper> {

	ObjectMapper mapper = new ObjectMapper();

	public ObjectResolver() {
		mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
	}
	@Override
	public ObjectMapper getContext(Class<?> type) {
		return mapper;
	}
}