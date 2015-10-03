package br.com.engine.context;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import br.com.commons.transport.UnitObject;
import br.com.commons.utils.PropertiesLoader;
import br.com.engine.persistence.beans.Unit;
import br.com.engine.persistence.cache.UnitCache;
import br.com.engine.persistence.dao.UnitDAO;

public class ApplicationContextProvider implements ApplicationContextAware,
		ApplicationListener<ContextRefreshedEvent> {
	private ApplicationContext context;

	private static PropertiesLoader propertiesLoader;

	private static final Logger LOGGER = Logger
			.getLogger(ApplicationContextProvider.class);

	@Override
	public void setApplicationContext(final ApplicationContext ctx)
			throws BeansException {
		AppContext.setApplicationContext(ctx);
		this.context = ctx;
	}

	/**
	 * @return Returns the context.
	 */
	public ApplicationContext getContext() {
		return this.context;
	}

	public static void setPropertiesLoader(
			final PropertiesLoader propertiesLoader) {
		ApplicationContextProvider.propertiesLoader = propertiesLoader;
	}

	public static PropertiesLoader getPropertiesLoader() {
		return propertiesLoader;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		UnitDAO UnitDAO = context.getBean(UnitDAO.class);
		List<Unit> list = UnitDAO.list();
		if (!list.isEmpty()) {
			int cont = 0;
			LOGGER.info("\n\n\nInitializing army's on application\n\n\n");
			for (Unit unit : list) {
				UnitObject army = unit.generateTransportObject();
				UnitCache.getInstance().addToMoviments(army);
				cont++;
			}
			LOGGER.info("Army's initialized: " + cont);
		} 
	}
}