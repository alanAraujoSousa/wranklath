package br.com.engine.service.rest.context;

import java.lang.reflect.Method;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.commons.annotations.Authorize;
import br.com.commons.enums.PermissionEnum;
import br.com.engine.business.controller.UserController;
import br.com.engine.service.rest.filters.AuthorizationRestFilter;

@Component
@Provider
public class AuthorizationFeature implements DynamicFeature {

	@Autowired
	UserController userController;

	@Override
	public void configure(ResourceInfo resourceInfo, FeatureContext context) {
		Method method = resourceInfo.getResourceMethod();
		if (method.isAnnotationPresent(Authorize.class)) {
			
			Authorize authenticate = method.getAnnotation(Authorize.class);
			PermissionEnum[] permissionsRequired = authenticate.value();
			
			AuthorizationRestFilter authenticationFilter = new AuthorizationRestFilter(
					userController, permissionsRequired);
			
			context.register(authenticationFilter);
		}
	}
}