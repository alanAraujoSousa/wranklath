package br.com.engine.controller.rest.context;

import java.lang.reflect.Method;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.commons.annotations.Authorize;
import br.com.commons.enums.PermissionEnum;
import br.com.engine.business.service.UserService;
import br.com.engine.controller.rest.filters.AuthorizationRestFilter;

@Component
@Provider
public class AuthorizationFeature implements DynamicFeature {

	@Autowired
	UserService userService;

	@Override
	public void configure(ResourceInfo resourceInfo, FeatureContext context) {
		Method method = resourceInfo.getResourceMethod();
		if (method.isAnnotationPresent(Authorize.class)) {
			
			PermissionEnum[] permissionsRequired = method.getAnnotation(Authorize.class).value();
			
			AuthorizationRestFilter authenticationFilter = new AuthorizationRestFilter(
					userService, permissionsRequired);
			
			context.register(authenticationFilter);
		}
	}
}