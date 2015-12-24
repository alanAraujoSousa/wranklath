package br.com.engine.business.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.commons.transport.UserGroupObject;
import br.com.engine.persistence.beans.UserGroup;
import br.com.engine.persistence.dao.UserGroupDAO;

public class UserGroupService {

	private static final Logger LOGGER = Logger.getLogger(UserService.class);
	@Autowired
	private UserGroupDAO userGroupDAO;

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<UserGroupObject> listAllGroups() {
		List<UserGroup> listGroup = userGroupDAO.list();
		final List<UserGroupObject> listUserGroupObject = new ArrayList<UserGroupObject>();
		for (final UserGroup userGroup : listGroup) {
			listUserGroupObject.add(userGroup.generateTransportObject());
		}

		return listUserGroupObject;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public UserGroupObject create(UserGroupObject userGroupObject) throws Exception {
		
		LOGGER.info("Registering Group: "+ userGroupObject.getName());
		
		userGroupObject = userGroupDAO.create(userGroupObject).generateTransportObject();
		return userGroupObject;
		
	}
// Delete group 
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void delete(String name)throws Exception {
		UserGroup find = userGroupDAO.find(name);
		this.userGroupDAO.destroy(find);
	}
	
}
