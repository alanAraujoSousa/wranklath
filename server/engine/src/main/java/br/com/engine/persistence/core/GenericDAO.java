package br.com.engine.persistence.core;

import java.lang.reflect.ParameterizedType;

import org.hibernate.Criteria;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public abstract class GenericDAO<T> {

	private HibernateUtil hibernateUtil;

	private Class<T> klass; 

	@SuppressWarnings("unchecked")
	public GenericDAO() {
		klass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).
				  getActualTypeArguments()[0];
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	protected void save(T entity) {
		hibernateUtil.currentSession().saveOrUpdate(entity);
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	protected void delete(T entity) {
		hibernateUtil.currentSession().delete(entity);
	}

	protected Criteria getCriteria() {
		return hibernateUtil.currentSession().createCriteria(klass);
	}

	protected void update(T entity) {
		hibernateUtil.currentSession().update(entity);
	}

	/**
	 * @param hibernateUtil
	 *            the hibernateUtil to set
	 */
	public void setHibernateUtil(HibernateUtil hibernateUtil) {
		this.hibernateUtil = hibernateUtil;
	}
	
	/**
	 * @return Returns the hibernateUtil.
	 */
	public HibernateUtil getHibernateUtil() {
		return hibernateUtil;
	}
}