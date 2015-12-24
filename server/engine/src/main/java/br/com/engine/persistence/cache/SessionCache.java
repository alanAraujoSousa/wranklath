package br.com.engine.persistence.cache;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import br.com.commons.transport.SessionObject;

public class SessionCache {
	

	public static final int DEFAULT_EXPIRATION = 10; // Default expiration in
														// minutes.
	private static SessionCache instance;
	private static Map<String, SessionObject> repository;

	private SessionCache() {
		repository = new ConcurrentHashMap<String, SessionObject>();
	}

	public static SessionCache getInstance() {
		if (instance == null) {
			instance = new SessionCache();
		}
		return instance;
	}

	/**
	 * Add session on cache.
	 * 
	 * @param session
	 */
	public void add(String token, SessionObject session) {
		getRepository().put(token, session);
	}

	/**
	 * Find session by user comparing login.
	 * 
	 * @param userObject
	 * @return
	 */
	public SessionObject findSessionByUser(String login) {
		Collection<SessionObject> values = getRepository().values();
		for (SessionObject sessionObject : values) {
			if (login.equals(sessionObject.getUser().getLogin())) {
				return sessionObject;
			}
		}
		return null;
	}

	/**
	 * Find session by token.
	 * 
	 * @param token
	 * @return
	 */
	public SessionObject findSessionByToken(String token) {
		SessionObject sessionObject = getRepository().get(token);
		return sessionObject;
	}

	/**
	 * Remove session.
	 * 
	 * @param token
	 */
	public void removeSession(String token) {
		getRepository().remove(token);
	}

	public void cleanExpiredSessions() {
		Date currentDate = new Date();
		for (Iterator<Entry<String, SessionObject>> iterator = getRepository().entrySet()
				.iterator(); iterator.hasNext();) {
			SessionObject sessionObject = iterator.next().getValue();
			if (sessionObject.getExpirationDate().before(currentDate)) {
				iterator.remove();
			}
		}
	}

	/**
	 * @return the repository
	 */
	public static Map<String, SessionObject> getRepository() {
		return repository;
	}
}
