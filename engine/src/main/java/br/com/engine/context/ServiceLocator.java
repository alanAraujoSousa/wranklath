package br.com.engine.context;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class ServiceLocator {
	private static ServiceLocator instance;

	private ApplicationContext appContext;

	private static final Logger LOGGER = Logger.getLogger(ServiceLocator.class);

	private static final String DEFAULT_APPLICATION_CONTEXT = "applicationContext.xml";

	private static Map<Class<?>, Object> helpersBeans = new LinkedHashMap<Class<?>, Object>();

	private ServiceLocator() {
	}

	/**
	 * Create a ServiceLocator instance using the applicationContext.xml located in file system
	 * 
	 * @throws BeansException
	 *             if context creation failed
	 * 
	 * @return
	 */
	public synchronized static ServiceLocator getInstance() {
		if (instance == null) {
			instance = new ServiceLocator();

			final ApplicationContext ctx = AppContext.getApplicationContext();
			if (ctx != null) {
				instance.appContext = ctx;
			} else {
				instance.startContext(DEFAULT_APPLICATION_CONTEXT);
			}
		}

		return instance;
	}

	/**
	 * Create a ServiceLocator instance using an specific application context file located in file system
	 * 
	 * @throws BeansException
	 *             if context creation failed
	 * 
	 * @return
	 */
	public synchronized static ServiceLocator getInstance(final String applicationContextLocation) {
		if (instance == null) {
			instance = new ServiceLocator();

			final ApplicationContext ctx = AppContext.getApplicationContext();
			if (ctx != null) {
				instance.appContext = ctx;
			} else {
				if (applicationContextLocation.startsWith("classpath")) {
					try {
						instance.startContext(new URI(applicationContextLocation.split(":")[1]));
					} catch (final URISyntaxException e) {
						e.printStackTrace();
					}
				} else {
					instance.startContext(applicationContextLocation);
				}
			}
		}

		return instance;
	}

	/**
	 * * Create a ServiceLocator instance using an specific application context file located in file system
	 * 
	 * @param applicationContextLocation
	 * @param forceContextRebuild
	 * 
	 * @throws BeansException
	 *             if context creation failed
	 * 
	 * @return
	 */
	public synchronized static ServiceLocator getInstance(final String applicationContextLocation,
			final boolean forceContextRebuild) {
		if (!forceContextRebuild) {
			return getInstance(applicationContextLocation);
		} else {
			instance = new ServiceLocator();

			final ApplicationContext ctx = AppContext.getApplicationContext();
			if (ctx != null) {
				instance.appContext = ctx;
			} else {
				instance.startContext(applicationContextLocation);
			}
		}

		return instance;
	}

	/**
	 * Create a ServiceLocator instance using an specific application context file located in classpath
	 * 
	 * @param applicationContextLocation
	 * @param forceContextRebuild
	 * 
	 * @throws BeansException
	 *             if context creation failed
	 * 
	 * @return
	 */
	public synchronized static ServiceLocator getInstance(final URI applicationContextLocation,
			final boolean forceContextRebuild) {
		if (!forceContextRebuild) {
			return getInstance(applicationContextLocation);
		} else {
			if (instance == null) {
				instance = new ServiceLocator();
			}

			instance.startContext(applicationContextLocation);

		}

		return instance;
	}

	/**
	 * Create a ServiceLocator instance using an specific application context file located in classpath
	 * 
	 * @throws BeansException
	 *             if context creation failed
	 * 
	 * @return
	 */
	public synchronized static ServiceLocator getInstance(final URI applicationContextLocation) {
		if (instance == null) {
			instance = new ServiceLocator();

			final ApplicationContext ctx = AppContext.getApplicationContext();
			if (ctx != null) {
				instance.appContext = ctx;
			} else {
				instance.startContext(applicationContextLocation);
			}
		}

		return instance;
	}

	private void startContext(final String applicationContextLocation) {
		this.appContext = new FileSystemXmlApplicationContext(applicationContextLocation);
	}

	private void startContext(final URI applicationContextLocation) {
		this.appContext = new ClassPathXmlApplicationContext(applicationContextLocation.toString());
	}

	/**
	 * Return the bean instance that uniquely matches the given object type, if any.
	 * 
	 * @param requiredType
	 *            type the bean must match; can be an interface or superclass. {@literal null} is disallowed.
	 * @return bean matching required type
	 * 
	 * @throws NoSuchBeanDefinitionException
	 *             if there is not exactly one matching bean found
	 * @throws FatalBeanException
	 *             if there is more than one matching bean found
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T> T lookupService(final Class<T> requiredType) throws BeansException {
		T bean = null;

		if (!helpersBeans.isEmpty()) {
			bean = (T) helpersBeans.get(requiredType);

			if (bean != null) {
				return bean;
			}
		}

		final Map<String, T> beansOfType = this.appContext.getBeansOfType(requiredType);
		if ((beansOfType != null) & (beansOfType.size() == 1)) {
			bean = beansOfType.values().iterator().next();
		} else if ((beansOfType != null) && (beansOfType.size() > 1)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("More than one bean declaration found for class: " + requiredType.getName());
			}

			throw new FatalBeanException("More than one bean declaration found for class: " + requiredType.getName());
		} else if (beansOfType.isEmpty()) {
			throw new NoSuchBeanDefinitionException("There's no such bean definition for class: "
					+ requiredType.getName());
		}

		return bean;
	}

	/**
	 * Return the bean instance that matches partially/fully the given bean name of give type
	 * 
	 * @param requiredType
	 *            type the bean must match; can be an interface or superclass. {@literal null} is disallowed.
	 * 
	 * @param beanName
	 *            name the name of the bean to retrieve
	 * 
	 * @param match
	 *            must match the full name of the bean to retrieve
	 * 
	 * @return bean matching required type
	 * 
	 * @throws NoSuchBeanDefinitionException
	 *             if there's no such bean definition
	 * @throws BeanNotOfRequiredTypeException
	 *             if the bean is not of the required type
	 * @throws BeansException
	 *             if the bean could not be created
	 * @throws FatalBeanException
	 *             if there is more than one matching bean found
	 */
	public synchronized <T> T lookupService(final String beanName, final Class<T> requiredType, final boolean match)
			throws BeansException {
		T bean = null;

		if (match == true) {
			bean = this.appContext.getBean(beanName, requiredType);
		} else {
			final Map<String, T> beans = new LinkedHashMap<String, T>();

			final Map<String, T> beansOfType = this.appContext.getBeansOfType(requiredType);

			// Creating beans map
			for (final Entry<String, T> entry : beansOfType.entrySet()) {
				final String key = entry.getKey();
				final T value = entry.getValue();

				if (key.contains(beanName)) {
					beans.put(key, value);
				}
			}

			// Removing beans different of desired type
			for (final Iterator<Map.Entry<String, T>> it = beans.entrySet().iterator(); it.hasNext();) {
				final Map.Entry<String, T> entry = it.next();
				final T value = entry.getValue();

				if (value.getClass() != requiredType) {
					it.remove();
				}
			}

			final int size = beans.size();
			if (size == 0) {
				throw new NoSuchBeanDefinitionException("There's no such bean definition for bean name: " + beanName);
			} else if (size == 1) {
				bean = beans.values().iterator().next();
			} else if (size > 1) {
				throw new FatalBeanException("More than one bean declaration found for bean name: " + beanName);
			}
		}

		return bean;
	}

	/**
	 * Return the bean instance that uniquely matches the given object type, if any.
	 * 
	 * @param requiredType
	 *            type the bean must match; can be an interface or superclass. {@literal null} is disallowed.
	 * 
	 * @param beanName
	 *            name the name of the bean to retrieve
	 * 
	 * @return bean matching required type
	 * 
	 * @throws NoSuchBeanDefinitionException
	 *             if there's no such bean definition
	 * @throws BeanNotOfRequiredTypeException
	 *             if the bean is not of the required type
	 * @throws BeansException
	 *             if the bean could not be created
	 * @throws FatalBeanException
	 *             if there is more than one matching bean found
	 */
	public synchronized <T> T lookupService(final String beanName, final Class<T> requiredType) throws BeansException {
		return lookupService(beanName, requiredType, true);
	}

	/**
	 * Lookup service based on service bean name.
	 * 
	 * @param serviceBeanName
	 *            the service bean name
	 * @return the service bean
	 * 
	 * @throws NoSuchBeanDefinitionException
	 *             if there's no such bean definition
	 */
	public synchronized Object lookupService(final String serviceBeanName) {
		return this.appContext.getBean(serviceBeanName);
	}
}
