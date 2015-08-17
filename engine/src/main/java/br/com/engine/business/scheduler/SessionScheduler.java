package br.com.engine.business.scheduler;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;

import br.com.engine.persistence.cache.SessionCache;

public class SessionScheduler {

	private static final Logger LOGGER = Logger
			.getLogger(SessionScheduler.class);

	/**
	 * Clear expired sessions every 10 minutes
	 */
	@Scheduled(fixedDelay = 1000 * 60 * 10)
	public void cleanSessions() {

		LOGGER.info("\n\n\n\n%%%%%\\o/%%%%%__Cleaning expired sessions__%%%%%\\o/%%%%%\n\n\n\n");

		SessionCache sessionCache = SessionCache.getInstance();
		sessionCache.cleanExpiredSessions();
	}
}