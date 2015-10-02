package br.com.engine.persistence.dao;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.commons.transport.PermissionObject;
import br.com.engine.persistence.beans.Permission;
import br.com.engine.persistence.core.GenericDAO;

public class PermissionDAO extends GenericDAO<Permission> {
	
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<Permission> list() {
		return getCriteria().list();
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public Permission create(PermissionObject permissionObject) throws Exception {
		Permission permission = new Permission();
		permission.setPermission(permissionObject.getPermission());
		save(permission);
		return permission;
	}
}
