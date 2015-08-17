package persistence;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import br.com.commons.enums.PermissionEnum;
import br.com.commons.transport.PermissionObject;
import br.com.commons.transport.UserGroupObject;
import br.com.commons.transport.UserObject;
import br.com.commons.utils.CryptUtil;
import br.com.engine.persistence.beans.Permission;
import br.com.engine.persistence.beans.UserGroup;
import br.com.engine.persistence.dao.PermissionDAO;
import br.com.engine.persistence.dao.UserDAO;
import br.com.engine.persistence.dao.UserGroupDAO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/applicationContext.xml" })
@Transactional
public class PopulateInitialDatabase {

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private UserGroupDAO userGroupDAO;

	@Autowired
	private PermissionDAO permissionDAO;

	@Test
	@Rollback(false)
	public void populeDatabase() throws Exception {
		saveAllPermissions();
		List<UserGroup> allGroups = saveAllGroups();
		saveAllUser(allGroups);
		assignPermissionToGroups();
		assignPermissionToUsers();
	}

	private void saveAllUser(List<UserGroup> listGroups) throws Exception {

		final UserObject root = new UserObject();
		root.setLogin("root");
		root.setPassword("root");
		root.setPassword(CryptUtil.getInstance().aesEncrypt(root.getPassword()));

		// Create root on Admin Group
		for (UserGroup group : listGroups) {
			if (group.getName().equalsIgnoreCase("admin")) {
				this.userDAO.create(root, group);
			}
		}

		// Create 10 users on Standart Group
		for (int i = 0; i < 10; i++) {
			UserObject user = new UserObject();
			user.setLogin("userStd" + i);
			user.setPassword("userStd" + i);
			user.setPassword(CryptUtil.getInstance().aesEncrypt(
					user.getPassword()));
			for (UserGroup group : listGroups) {
				if (group.getName().equalsIgnoreCase("standart")) {
					this.userDAO.create(user, group);
				}
			}
		}

		// Create 10 users on Premium Group
		for (int i = 0; i < 10; i++) {
			UserObject user = new UserObject();
			user.setLogin("userPrm" + i);
			user.setPassword("userPrm" + i);
			user.setPassword(CryptUtil.getInstance().aesEncrypt(
					user.getPassword()));
			for (UserGroup group : listGroups) {
				if (group.getName().equalsIgnoreCase("premium")) {
					this.userDAO.create(user, group);
				}
			}
		}
	}

	private void saveAllPermissions() throws Exception {
		for (PermissionEnum permissionEnum : PermissionEnum.values()) {
			PermissionObject permissionObject = new PermissionObject();
			permissionObject.setPermission(permissionEnum);
			this.permissionDAO.create(permissionObject);
		}
	}

	private List<UserGroup> saveAllGroups() throws Exception {

		List<UserGroupObject> listGroupObject = new ArrayList<UserGroupObject>();

		final UserGroupObject adminGroup = new UserGroupObject();
		adminGroup.setName("Admin");

		final UserGroupObject PremiumGroup = new UserGroupObject();
		PremiumGroup.setName("Premium");

		final UserGroupObject standartGroup = new UserGroupObject();
		standartGroup.setName("Standart");

		listGroupObject.add(adminGroup);
		listGroupObject.add(PremiumGroup);
		listGroupObject.add(standartGroup);

		List<UserGroup> listGroup = new ArrayList<UserGroup>();
		for (UserGroupObject group : listGroupObject) {
			listGroup.add(this.userGroupDAO.create(group));
		}

		return listGroup;
	}

	private void assignPermissionToGroups() {

		List<Permission> listPermission = this.permissionDAO.list();
		List<UserGroup> listGroup = this.userGroupDAO.list();

		// Assign Admin permission on Admin group
		for (UserGroup userGroup : listGroup) {
			if (userGroup.getName().equalsIgnoreCase("Admin")) {
				HashSet<Permission> permissionsForAdmin = new HashSet<Permission>();
				for (Permission permission : listPermission) {
					if (permission.getPermission().equals(PermissionEnum.ADMIN)) {
						permissionsForAdmin.add(permission);
					}
				}
				userGroupDAO.setPermissions(userGroup, permissionsForAdmin);
			}
		}
	}

	private void assignPermissionToUsers() {

	}
}
