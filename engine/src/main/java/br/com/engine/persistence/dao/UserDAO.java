package br.com.engine.persistence.dao;

import java.util.List;
import java.util.Set;

import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.commons.transport.UserObject;
import br.com.engine.persistence.beans.Permission;
import br.com.engine.persistence.beans.User;
import br.com.engine.persistence.beans.UserGroup;
import br.com.engine.persistence.core.GenericDAO;

public class UserDAO extends GenericDAO<User> {
	
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public User create(UserObject userObject, UserGroup userGroup) {
		User user = new User();
		user.setLogin(userObject.getLogin());
		user.setPassword(userObject.getPassword());
		user.setName(userObject.getName());
		user.setUserGroup(userGroup);
		save(user);
		return user;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<User> list() {
		return getCriteria().list();
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public User find(String login, String password) {
		return (User) getCriteria()
				.add(Restrictions.eq("login", login))
				.add(Restrictions.eq("password", password)).uniqueResult();
	}
	
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public User find(String login) {
		return (User) getCriteria()
				.add(Restrictions.eq("login", login)).uniqueResult();
	}
	
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void destroy(User user) {
		delete(user);
	}	
	
	
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void setPermissions(User user, Set<Permission> listPermission) {
		user.setPermissions(listPermission);
		this.update(user);
	}
}
