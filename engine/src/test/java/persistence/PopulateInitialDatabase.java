package persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import br.com.commons.enums.BuildingTypeEnum;
import br.com.commons.enums.PermissionEnum;
import br.com.commons.enums.UnitTypeEnum;
import br.com.commons.transport.BuildingObject;
import br.com.commons.transport.PermissionObject;
import br.com.commons.transport.UnitObject;
import br.com.commons.transport.UserGroupObject;
import br.com.commons.transport.UserObject;
import br.com.commons.utils.CryptUtil;
import br.com.commons.utils.Utils;
import br.com.engine.persistence.beans.Permission;
import br.com.engine.persistence.beans.Place;
import br.com.engine.persistence.beans.User;
import br.com.engine.persistence.beans.UserGroup;
import br.com.engine.persistence.core.HibernateUtil;
import br.com.engine.persistence.dao.BuildingDAO;
import br.com.engine.persistence.dao.PermissionDAO;
import br.com.engine.persistence.dao.PlaceDAO;
import br.com.engine.persistence.dao.UnitDAO;
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
	private BuildingDAO buildingDAO;

	@Autowired
	private UnitDAO unitDAO;

	@Autowired
	private PermissionDAO permissionDAO;
	
	@Autowired
	private PlaceDAO placeDAO;
	
	@Test
	@Rollback(false)
	public void populeDatabase() throws Exception {
		// +++++++++++++++++__WARNING__++++++++++++++++++
		// Heavy database operation.

//		 saveMap();

		// Heavy database operation.
		// +++++++++++++++++__WARNING__++++++++++++++++++

		saveAllPermissions();
		saveAllGroups();
		saveAllUser();
		saveAllTowns();
		saveAllArmys();
		assignPermissionToGroups();
		assignPermissionToUsers();
	}

	private void saveMap() {
		Date startDateOfBatchProcess = new Date();
		
		for (int x = 1; x <= Utils.MAP_SIZE; x++) {
			System.out.println("Commiting x: " + x + " fill 1000 Y's.");
			for (int y = 1; y <= Utils.MAP_SIZE; y++) {
				Place place = new Place(x, y);
				HibernateUtil.getInstance().currentSession().save(place);
			}
			HibernateUtil.getInstance().currentSession().flush();
			HibernateUtil.getInstance().currentSession().clear();
		}
		Date endDateOfBatchProcess = new Date();
		System.out
				.println("Time spent (in minutes) with batch processing: "
						+ ((endDateOfBatchProcess.getTime() - startDateOfBatchProcess
								.getTime()) / 60000));
	}

	private List<User> saveAllUser() throws Exception {
		
		List<UserGroup> listGroups = this.userGroupDAO.list();

		List<User> users = new ArrayList<>();

		final UserObject root = new UserObject();
		root.setLogin("root");
		root.setPassword("root");
		root.setPassword(CryptUtil.getInstance().aesEncrypt(root.getPassword()));

		// Create root on Admin Group
		for (UserGroup group : listGroups) {
			if (group.getName().equalsIgnoreCase("admin")) {
				User user = this.userDAO.create(root, group);
				users.add(user);
			}
		}

		// Create 10 users on Standart Group
		for (int i = 0; i < 10; i++) {
			UserObject userObject = new UserObject();
			userObject.setLogin("userStd" + i);
			userObject.setPassword("userStd" + i);
			userObject.setPassword(CryptUtil.getInstance().aesEncrypt(
					userObject.getPassword()));
			for (UserGroup group : listGroups) {
				if (group.getName().equalsIgnoreCase("standart")) {
					User user = this.userDAO.create(userObject, group);
					users.add(user);
				}
			}
		}

		// Create 10 users on Premium Group
		for (int i = 0; i < 10; i++) {
			UserObject userObject = new UserObject();
			userObject.setLogin("userPrm" + i);
			userObject.setPassword("userPrm" + i);
			userObject.setPassword(CryptUtil.getInstance().aesEncrypt(
					userObject.getPassword()));
			for (UserGroup group : listGroups) {
				if (group.getName().equalsIgnoreCase("premium")) {
					User user = this.userDAO.create(userObject, group);
					users.add(user);
				}
			}
		}
		return users;
	}

	private void saveAllTowns() {
		List<User> allUsers = this.userDAO.list();
		Date now = new Date();
		int randomCoordinate = 300;
		for (User user : allUsers) {
			BuildingObject buildingObject = new BuildingObject();
			buildingObject.setConclusionDate(now);
			buildingObject.setType(BuildingTypeEnum.TOWN);
			Place place = this.placeDAO.findByCoordinates(randomCoordinate, randomCoordinate);
			this.buildingDAO.create(buildingObject, user, place);
			randomCoordinate += 30;
		}
	}
	
	private void saveAllArmys() {
		List<User> allUsers = this.userDAO.list();
		int randomCoordinate = 50;
		UnitTypeEnum[] values = UnitTypeEnum.values();
		for (User user : allUsers) {
			UnitObject unitObject = new UnitObject();
			unitObject.setType(values[new Random().nextInt(3)]);
			unitObject.setQuantity(30);
			Place place = this.placeDAO.findByCoordinates(randomCoordinate, randomCoordinate);
			unitDAO.create(unitObject, user, place);
			randomCoordinate += 30;
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
