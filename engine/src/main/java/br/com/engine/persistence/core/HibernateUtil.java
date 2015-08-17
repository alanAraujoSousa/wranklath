package br.com.engine.persistence.core;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.springframework.orm.hibernate4.HibernateTemplate;

public class HibernateUtil {

	private final static Map<String, SessionFactory> factorys = new HashMap<String, SessionFactory>();
	private HibernateTemplate hibernateTemplate;
	private static ServiceRegistry serviceRegistry;

	private static String DEFAULT_CONFIGURATION = "hibernateDefault";

	private static HibernateUtil instance;

	private HibernateUtil() {
		if (hibernateTemplate != null) {
			System.out.println("muita treta vixe");
		}
	}

	private HibernateUtil(final HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	public static HibernateUtil getInstance() {
		if (null == instance) {
			instance = new HibernateUtil();

			try {
				SessionFactory factory = null;
				final Configuration conf = new Configuration().configure();

				serviceRegistry = new StandardServiceRegistryBuilder()
						.applySettings(conf.getProperties()).build();
				factory = conf.buildSessionFactory(serviceRegistry);

				factorys.put(DEFAULT_CONFIGURATION, factory);
				
				final ConfigFactory config = ConfigFactory.getInstance();
				if (config.isLoad()) {
					final Enumeration<String> configurations = config.getKeys();
					while (configurations.hasMoreElements()) {
						final String key = configurations.nextElement();
						final String configuracao = config.getValorString(key);

						final Configuration configuration = new Configuration()
								.configure(configuracao);

						final ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
								.applySettings(configuration.getProperties())
								.build();
						factory = configuration
								.buildSessionFactory(serviceRegistry);

						factorys.put(key, factory);
					}
				}
			} catch (final Throwable ex) {
				ex.printStackTrace();
				throw new ExceptionInInitializerError(ex);
			}
		}

		return instance;
	}

	public static HibernateUtil init(final HibernateTemplate hibernateTemplate) {
		if (null == instance) {
			instance = new HibernateUtil(hibernateTemplate);

			SessionFactory factory = null;
			factory = hibernateTemplate.getSessionFactory();

			factorys.put(DEFAULT_CONFIGURATION, factory);
		}

		return instance;
	}

	public Session currentSession() throws HibernateException {
		return instance.hibernateTemplate.getSessionFactory()
				.getCurrentSession();
	}
	
	public void closeSession() throws HibernateException {
		if (instance.hibernateTemplate.getSessionFactory().getCurrentSession() != null) {
			instance.hibernateTemplate.getSessionFactory().getCurrentSession()
					.close();
		}
	}

	String getDefaultConfiguration() {
		return DEFAULT_CONFIGURATION;
	}

	/**
	 * @return Returns the hibernateTemplate.
	 */
	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	/**
	 * @param hibernateTemplate the hibernateTemplate to set
	 */
	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}
}