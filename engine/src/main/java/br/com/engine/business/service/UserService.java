package br.com.engine.business.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.commons.enums.PermissionEnum;
import br.com.commons.exceptions.InvalidLoginException;
import br.com.commons.exceptions.InvalidPermissionException;
import br.com.commons.exceptions.InvalidTokenException;
import br.com.commons.transport.BuildingObject;
import br.com.commons.transport.PlaceObject;
import br.com.commons.transport.SessionObject;
import br.com.commons.transport.UnitObject;
import br.com.commons.transport.UserGroupObject;
import br.com.commons.transport.UserObject;
import br.com.commons.utils.CryptUtil;
import br.com.commons.utils.Utils;
import br.com.engine.persistence.beans.Building;
import br.com.engine.persistence.beans.Place;
import br.com.engine.persistence.beans.Session;
import br.com.engine.persistence.beans.Unit;
import br.com.engine.persistence.beans.User;
import br.com.engine.persistence.beans.UserGroup;
import br.com.engine.persistence.cache.SessionCache;
import br.com.engine.persistence.dao.BuildingDAO;
import br.com.engine.persistence.dao.PlaceDAO;
import br.com.engine.persistence.dao.SessionDAO;
import br.com.engine.persistence.dao.UnitDAO;
import br.com.engine.persistence.dao.UserDAO;
import br.com.engine.persistence.dao.UserGroupDAO;

@Transactional(propagation = Propagation.REQUIRED)
public class UserService {

	private static final Logger LOGGER = Logger.getLogger(UserService.class);

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private UnitDAO unitDAO;

	@Autowired
	private PlaceDAO placeDAO;

	@Autowired
	private BuildingDAO buildingDAO;

	@Autowired
	private UserGroupDAO userGroupDAO;

	@Autowired
	private SessionDAO sessionDAO;

	/**
	 * Perform a login into the system using an username and password and attach
	 * session
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String login(String username, String password)
			throws InvalidLoginException {

		// Encrypt password for comparision with the password saved in the
		// database.
		// All passwords in the database are encrypted.
		password = CryptUtil.getInstance().aesEncrypt(password);

		User user = userDAO.find(username, password);

		if (user == null) {
			throw new InvalidLoginException("Invalid login for user: "
					+ username);
		} else {
			SessionCache cacheProvider = SessionCache.getInstance();
			SessionObject sessionObject = cacheProvider.findSessionByUser(user
					.getLogin());
			if (sessionObject != null) // if have session in cache.
				return sessionObject.getToken();

			try { // if not.
				Session session = this.sessionDAO.create(user);
				String token = session.getToken();
				cacheProvider.add(token, session.generateTransportObject());
				return token;
			} catch (final Exception e) {
				throw new InvalidLoginException("Error on creating session: ",
						e);
			}
		}
	}

	/**
	 * Perform logout destroying session attached
	 * 
	 * @param token
	 * @throws InvalidTokenException
	 * @throws InvalidLoginException
	 */
	public void logout(final String token) throws InvalidTokenException,
			InvalidLoginException {
		SessionCache sessionCache = SessionCache.getInstance();
		sessionCache.removeSession(token);
	}

	/**
	 * Validate if a token exists and isn't expired.
	 * 
	 * @param token
	 * @return
	 */
	public boolean isTokenValid(final String token) {
		boolean result = false;
		SessionObject sessionObject = SessionCache.getInstance()
				.findSessionByToken(token);
		if (sessionObject != null) {
			if (!sessionObject.isExpired()) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * Extend token validation
	 * 
	 * @param token
	 * @return
	 */
	public SessionObject extendTokenExpiration(final String token) {
		SessionObject session = SessionCache.getInstance().findSessionByToken(
				token);
		return this.extendTokenExpiration(session);
	}

	/**
	 * Extend token validation
	 * 
	 * @param token
	 * @return
	 */
	public SessionObject extendTokenExpiration(SessionObject session) {
		if (session != null && !session.isExpired()) {
			final Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MINUTE, SessionCache.DEFAULT_EXPIRATION);
			final Date expirationDate = calendar.getTime();
			session.setExpirationDate(expirationDate);
		}
		return session;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void delete(String userLogin) throws Exception {
		User find = userDAO.find(userLogin);
		this.userDAO.destroy(find);
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public UserObject create(UserObject userObject) throws Exception {

		/*
		 * TODO Business rules.. like.. send email to anyone wishing to
		 * register.
		 */

		LOGGER.info("Registering user: " + userObject.getLogin());

		userObject.setPassword(CryptUtil.getInstance().aesEncrypt(
				userObject.getPassword()));

		// Default group, all user must be in one group.
		// TODO Define constant with default group name.
		UserGroup userGroup = userGroupDAO.find("Standart");

		userObject = userDAO.create(userObject, userGroup)
				.generateTransportObject();
		return userObject;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<Object> listAllVisible(UserObject userObject) {

		Set<PlaceObject> listAllPlacesVisible = new HashSet<PlaceObject>();
		Long userId = userObject.getId();
		Set<PlaceObject> placesDenied = new HashSet<PlaceObject>();

		// Code replication I KNOW!
		List<Unit> units = this.unitDAO.listByUser(userId);
		for (Unit unit : units) {
			Place place = unit.getPlace();
			Set<PlaceObject> visibles = Utils.listAllPlacesVisible(unit
					.getType().getVisibility(), place.getX(), place.getY());
			
			placesDenied.add(place.generateTransportObject());
			listAllPlacesVisible.addAll(visibles);
		}
		List<Building> buildings = this.buildingDAO.listByUser(userId);
		for (Building build : buildings) {
			Place place = build.getPlace();
			// FIXME improve visibility property on buildings.
			int miau = 3;
			Set<PlaceObject> visibles = Utils.listAllPlacesVisible(miau,
					place.getX(), place.getY());
						
			placesDenied.add(place.generateTransportObject());
			listAllPlacesVisible.addAll(visibles);
		}
		
		listAllPlacesVisible.removeAll(placesDenied);
		
		Set<UnitObject> unitObjects = new HashSet<UnitObject>();
		Set<BuildingObject> buildingsObjects = new HashSet<BuildingObject>();
		for (PlaceObject placeVisible : listAllPlacesVisible) {
			Place place = this.placeDAO.findByCoordinates(placeVisible.getX(),
					placeVisible.getY());
			Building building = place.getBuilding();
			if (building != null) {
				buildingsObjects.add(building.generateTransportObject());
			}
			Unit unit = place.getUnit();
			if (unit != null) {
				unitObjects.add(unit.generateTransportObject());
			}
		}

		List<Object> list = new ArrayList<Object>();
		list.addAll(unitObjects);
		list.addAll(buildingsObjects);
		
		return list;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<UserObject> listAllUsers() {
		List<User> listUser = userDAO.list();
		final List<UserObject> listUserObject = new ArrayList<UserObject>();
		for (final User user : listUser) {
			listUserObject.add(user.generateTransportObject());
		}
		return listUserObject;
	}

	public UserObject findUserBySessionToken(String token) {
		SessionObject session = findSessionByToken(token);
		return session.getUser();
	}

	public SessionObject findSessionByToken(String token) {
		SessionObject session = SessionCache.getInstance().findSessionByToken(
				token);
		if (session != null)
			return session;
		else
			throw new InvalidTokenException("Token don't have a valid session.");
	}

	public void authorize(PermissionEnum[] permissionsRequired, String token) {
		SessionObject session = findSessionByToken(token);

		UserObject user = session.getUser();
		Set<Integer> userPermissions = user.getPermissions();
		UserGroupObject userGroup = user.getUserGroup();
		if (userGroup != null) {
			Set<Integer> groupPermissions = userGroup.getPermissions();
			userPermissions.addAll(groupPermissions);
		}

		// Admin rule them all \o/ vlw flws
		if (userPermissions.contains(PermissionEnum.ADMIN.getId()))
			return;

		// LOGGING
		StringBuilder stringBuilder = new StringBuilder();
		for (PermissionEnum permissionRequired : permissionsRequired) {
			stringBuilder.append(permissionRequired.getPermission() + "\n");
		}
		LOGGER.debug("User: " + user.getLogin() + " need permissions: "
				+ stringBuilder);
		// LOGGING
		for (PermissionEnum permissionRequired : permissionsRequired) {
			boolean flag = false;
			for (Integer permissionObject : userPermissions) {
				if (permissionRequired.getId() == permissionObject) {
					flag = true;
					break;
				}
			}
			if (!flag) {
				throw new InvalidPermissionException(
						"You don't have the permissions required");
			}
		}
		this.extendTokenExpiration(session);
	}
}
