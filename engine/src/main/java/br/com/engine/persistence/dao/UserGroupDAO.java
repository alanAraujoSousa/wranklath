package br.com.engine.persistence.dao;

import java.util.List;
import java.util.Set;

import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.commons.transport.UserGroupObject;
import br.com.engine.persistence.beans.Permission;
import br.com.engine.persistence.beans.UserGroup;
import br.com.engine.persistence.core.GenericDAO;

public class UserGroupDAO extends GenericDAO<UserGroup> {

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	@SuppressWarnings("unchecked")
	public List<UserGroup> list() {
		return this.getCriteria().list();
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public UserGroup find(String name) {
		return (UserGroup) getCriteria().add(Restrictions.eq("name", name))
				.uniqueResult();
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public UserGroup create(UserGroupObject userGroupObject) throws Exception {
		final UserGroup group = new UserGroup();
		group.setName(userGroupObject.getName());
		this.save(group);
		return group;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void destroy(UserGroup group) throws Exception {
		this.destroy(group);
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void setPermissions(UserGroup userGroup,	Set<Permission> listPermission) {
		userGroup.setPermissions(listPermission);
		this.update(userGroup);
	}
}
