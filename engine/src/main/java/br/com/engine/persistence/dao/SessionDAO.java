package br.com.engine.persistence.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.commons.utils.Utils;
import br.com.engine.persistence.beans.Session;
import br.com.engine.persistence.beans.User;
import br.com.engine.persistence.cache.SessionCache;
import br.com.engine.persistence.core.GenericDAO;

public class SessionDAO extends GenericDAO<Session>{
	
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<Session> list() {
		return getCriteria().list();
	}
	
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Session findByUser(User user) {
		return (Session) getCriteria()
				.add(Restrictions.eq("user.id", user.getId())).list();
	}
	
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Session findByToken(String token) {
		return (Session) getCriteria()
				.add(Restrictions.eq("token", token)).uniqueResult();
	}
	
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public Session create(User user) throws Exception {
		Session session = new Session();
		session.setUser(user);
		session.setToken(Utils.generateRandomUUID());
		session.setRegisterDate(new Date());
		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, SessionCache.DEFAULT_EXPIRATION); // Add initial timeout for session.
		session.setExpirationDate(calendar.getTime());
		save(session);
		return session;
	}
}
