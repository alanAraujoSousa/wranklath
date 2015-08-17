package br.com.engine.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import br.com.commons.utils.PropertiesLoader;

public class ApplicationContextProvider implements ApplicationContextAware {
	private ApplicationContext context;

	private static PropertiesLoader propertiesLoader;

	@Override
	public void setApplicationContext(final ApplicationContext ctx) throws BeansException {
		AppContext.setApplicationContext(ctx);
		this.context = ctx;
	}

	/**
	 * @return Returns the context.
	 */
	public ApplicationContext getContext() {
		return this.context;
	}

	public static void setPropertiesLoader(final PropertiesLoader propertiesLoader) {
		ApplicationContextProvider.propertiesLoader = propertiesLoader;
	}

	public static PropertiesLoader getPropertiesLoader() {
		return propertiesLoader;
	}
}