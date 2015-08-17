package br.com.engine.context;

import org.springframework.context.ApplicationContext;

public class AppContext {
	private static ApplicationContext ctx;

	/**
	 * Injected from the class "ApplicationContextProvider" which is automatically loaded during Spring-Initialization.
	 */
	public static void setApplicationContext(final ApplicationContext applicationContext) {
		ctx = applicationContext;
	}

	/**
	 * Get access to the Spring ApplicationContext from everywhere in your Application.
	 * 
	 * @return
	 */
	public static ApplicationContext getApplicationContext() {
		return ctx;
	}
}
